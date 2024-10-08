package com.androidcoursehogent.postealapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.data.CommentData
import com.androidcoursehogent.postealapp.data.Event
import com.androidcoursehogent.postealapp.data.PostData
import com.androidcoursehogent.postealapp.data.UserData
import com.androidcoursehogent.postealapp.main.navigateTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.UninitializedMessageException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

const val USERS = "users"
const val POSTS = "posts"
const val COMMENTS = "comments"

@HiltViewModel
class PostealappViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val popupNotification = mutableStateOf<Event<String>?>(null)

    val refreshPostsProgress = mutableStateOf(false)
    val posts = mutableStateOf<List<PostData>>(listOf())

    val searchedPosts = mutableStateOf<List<PostData>>(listOf())
    val searchedPostProgress = mutableStateOf(false)

    val postsFeed = mutableStateOf<List<PostData>>(listOf())
    val postsFeedProgress = mutableStateOf(false)

    val comments = mutableStateOf<List<CommentData>>(listOf())
    val commentsProgress = mutableStateOf(false)

    val followers = mutableStateOf(0)

    var selectedLocation: String? by mutableStateOf(null)

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun onSignup(username: String, email: String, pass: String) {

        if (username.isEmpty() or email.isEmpty() or pass.isEmpty()) {
            handleException(customMessage = "Please complete all fields")
            return
        }

        inProgress.value = true
        db.collection(USERS).whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    handleException(customMessage = "Username already exists")
                    inProgress.value = false
                } else {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                createOrUpdateProfile(username = username)
                                //Perfil creado
                            } else {
                                handleException(task.exception, "Signup failed")
                            }
                            inProgress.value = false
                        }
                }
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            username = username ?: userData.value?.username,
            bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            following = userData.value?.following
        )

        uid?.let { uid ->
            inProgress.value = true
            db.collection(USERS).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(userData.toMap())
                        .addOnSuccessListener {
                            this.userData.value = userData
                            inProgress.value = false
                        }
                        .addOnFailureListener {
                            handleException(it, "Cannot update user")
                            inProgress.value = false
                        }
                } else {
                    db.collection(USERS).document(uid).set(userData)
                    getUserData(uid)
                    inProgress.value = false
                }
            }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot create user")
                    inProgress.value = false
                }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USERS).document(uid).get()
            .addOnSuccessListener {
                val user = it.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                refreshPosts()
                getPersonalizedFeed()
                getFollowers(user?.userId)
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Cannot retrieve user data")
                inProgress.value = false
            }
    }

    fun onLogin(email: String, pass: String) {

        if (email.isEmpty() or pass.isEmpty()) {
            handleException(customMessage = "Please complete all fields")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let { uid ->
                        getUserData(uid)
                    }
                } else {
                    handleException(task.exception, "Login failed")
                    inProgress.value = false
                }
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Login failed")
                inProgress.value = false
            }

    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)
    }

    fun updateProfileData(name: String, username: String, bio: String) {
        createOrUpdateProfile(name, username, bio)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {

        inProgress.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }
            .addOnFailureListener { exc ->
                handleException(exc)
                inProgress.value = false
            }

    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
            updatePostUserImageData(it.toString())
        }
    }

    private fun updatePostUserImageData(imageUrl: String){

        val currentUid = auth.currentUser?.uid
        db.collection(POSTS).whereEqualTo("userId", currentUid).get()
            .addOnSuccessListener {
                val posts = mutableStateOf<List<PostData>>(arrayListOf())
                convertPosts(it,posts)
                val refs = arrayListOf<DocumentReference>()

                for (post in posts.value){
                    post.postId?.let { id ->
                        refs.add(db.collection(POSTS).document(id))
                    }
                }

                if(refs.isNotEmpty()) {
                    db.runBatch { batch ->
                        for (ref in refs) {
                            batch.update(ref, "userImage", imageUrl)
                        }
                    }
                        .addOnSuccessListener {
                            refreshPosts()
                        }
                }
            }

    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged out")
        searchedPosts.value = listOf()
        postsFeed.value = listOf()
        comments.value = listOf()
    }

    fun onNewPost(uri: Uri, description: String, location: String? = null, onPostSuccess: () -> Unit) {
        uploadImage(uri) {
            onCreatePost(it, description, location, onPostSuccess)
        }
    }

    private fun onCreatePost(imageUri: Uri, description: String, location: String? = null, onPostSuccess: () -> Unit) {

        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.username
        val currentUserImage = userData.value?.imageUrl

        if (currentUid != null) {

            val postUuid = UUID.randomUUID().toString()

            val fillerWords = listOf("the", "be", "to", "is", "of", "and", "or", "a", "an", "in", "it", "am", "are", "have", "has")
            val searchTerms = description
                .split(" ", ".", ",", "?", "!", "#")
                .map { it.lowercase() }
                .filter { it.isNotEmpty() && !fillerWords.contains(it) }

            // Crear el objeto PostData con todos los detalles, incluida la ubicación
            val post = PostData(
                postId = postUuid,
                userId = currentUid,
                username = currentUsername,
                userImage = currentUserImage,
                postImage = imageUri.toString(),
                postDescription = description,
                time = System.currentTimeMillis(),
                likes = listOf<String>(),
                searchTerms = searchTerms,
                location = location  // Incluye la ubicación si está disponible
            )

            // Guardar el post en Firestore
            db.collection(POSTS).document(postUuid).set(post)
                .addOnSuccessListener {
                    popupNotification.value = Event("Post successfully created")
                    inProgress.value = false
                    refreshPosts()
                    onPostSuccess.invoke()
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Unable to create post")
                    inProgress.value = false
                }

        } else {
            handleException(customMessage = "Error: user ID unavailable. Unable to create post")
            onLogout()
            inProgress.value = false
        }
    }


    private fun refreshPosts(onRefreshed: () -> Unit = {}) {
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            refreshPostsProgress.value = true
            db.collection(POSTS).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, posts)
                    refreshPostsProgress.value = false
                    onRefreshed() // Llamar al callback después de refrescar
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot fetch posts")
                    refreshPostsProgress.value = false
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to refresh posts")
            onLogout()
        }
    }


    private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostData>>){
        val newPosts = mutableListOf<PostData>()
        documents.forEach { doc ->
            val post = doc.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPosts = newPosts.sortedByDescending { it.time }
        outState.value = sortedPosts
    }

    fun searchPosts(searchTerm: String) {
        if (searchTerm.isNotEmpty()) {
            searchedPostProgress.value = true

            // Realizar la búsqueda en los términos de búsqueda de los posts
            val postsTask = db.collection(POSTS)
                .whereArrayContains("searchTerms", searchTerm.trim().lowercase())
                .get()

            // Realizar la búsqueda en la colección de usuarios por nombre o nombre de usuario
            val usersTask = db.collection(USERS)
                .whereEqualTo("username", searchTerm.trim().lowercase())
                .get()
                .continueWithTask { task ->
                    if (task.isSuccessful && !task.result.isEmpty) {
                        val userId = task.result.documents.firstOrNull()?.id
                        db.collection(POSTS)
                            .whereEqualTo("userId", userId)
                            .get()
                    } else {
                        // Si no se encuentra usuario, buscar por nombre
                        db.collection(USERS)
                            .whereEqualTo("name", searchTerm.trim().lowercase())
                            .get()
                            .continueWithTask { nameTask ->
                                if (nameTask.isSuccessful && !nameTask.result.isEmpty) {
                                    val userId = nameTask.result.documents.firstOrNull()?.id
                                    db.collection(POSTS)
                                        .whereEqualTo("userId", userId)
                                        .get()
                                } else {
                                    null // No se encontraron usuarios ni por nombre ni por username
                                }
                            }
                    }
                }

            // Combinar ambos resultados
            postsTask.continueWithTask { postsTask ->
                usersTask.continueWith { usersTask ->
                    val combinedPosts = mutableListOf<PostData>()
                    if (postsTask.isSuccessful) {
                        postsTask.result?.let { documents ->
                            combinedPosts.addAll(documents.toObjects(PostData::class.java))
                        }
                    }
                    if (usersTask.isSuccessful) {
                        usersTask.result?.let { documents ->
                            combinedPosts.addAll(documents.toObjects(PostData::class.java))
                        }
                    }
                    combinedPosts
                }
            }.addOnSuccessListener { combinedPosts ->
                val sortedPosts = combinedPosts.sortedByDescending { it.time }
                searchedPosts.value = sortedPosts
                searchedPostProgress.value = false
            }.addOnFailureListener { exc ->
                handleException(exc, "Cannot search posts")
                searchedPostProgress.value = false
            }
        }
    }


    fun onFollowClick(userId: String) {

        auth.currentUser?.uid?.let{ currentUser ->
            val following = arrayListOf<String>()

            userData.value?.following?.let{
                following.addAll(it)
            }

            if(following.contains(userId)){
                following.remove(userId)
            } else{
                following.add(userId)
            }
            db.collection(USERS).document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)
                }
        }

    }

    private fun getPersonalizedFeed(){

        val following = userData.value?.following

        if(!following.isNullOrEmpty()){
            postsFeedProgress.value = true

            db.collection(POSTS).whereIn("userId", following).get()
                .addOnSuccessListener {
                    convertPosts(documents = it, postsFeed)
                    if(postsFeed.value.isEmpty()) {
                        getGeneralFeed()
                    } else{
                        postsFeedProgress.value = false
                    }
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot get personalized feed")
                    postsFeedProgress.value = false
                }
        } else{
            getGeneralFeed()
        }

    }

    private fun getGeneralFeed() {

        postsFeedProgress.value = true
        val currentTime = System.currentTimeMillis()
        val difference = 24 * 3600 * 1000 // 1 day
        db.collection(POSTS)
            .whereGreaterThan("time", currentTime - difference)
            .get()
            .addOnSuccessListener {
                convertPosts(documents = it, outState = postsFeed)
                postsFeedProgress.value = false
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Cannot get feed")
                postsFeedProgress.value = false
            }

    }

    fun onLikePost(postData: PostData) {

        auth.currentUser?.uid?.let { userId ->
            val likes = postData.likes ?: mutableListOf()
            val newLikes = if (likes.contains(userId)) {
                likes.filter { it != userId } // Quitar "me gusta"
            } else {
                likes + userId // Agregar "me gusta"
            }

            postData.postId?.let { postId ->
                db.collection(POSTS).document(postId).update("likes", newLikes)
                    .addOnSuccessListener {
                        // Actualizar la lista de likes del post en la UI
                        postData.likes = newLikes
                        // Refrescar los posts del usuario actual para que la UI se actualice
                        refreshPosts()
                    }
                    .addOnFailureListener {
                        handleException(it, "Unable to update likes")
                    }
            }
        }

    }

    fun observePostLikes(postId: String) {

        db.collection(POSTS).document(postId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("PostLikes", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val updatedPost = snapshot.toObject(PostData::class.java)
                    updatedPost?.let {
                        val currentPosts = posts.value.toMutableList()
                        val index = currentPosts.indexOfFirst { it.postId == postId }
                        if (index != -1) {
                            currentPosts[index] = it
                            posts.value = currentPosts
                        } else {
                            // Si el post no está en la lista, agrégalo
                            currentPosts.add(it)
                            posts.value = currentPosts
                        }
                    }
                }
            }

    }


    private fun updatePostInList(updatedPost: PostData) {

        val currentPosts = posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.postId == updatedPost.postId }
        if (index != -1) {
            currentPosts[index] = updatedPost
            posts.value = currentPosts // Notificar a la UI del cambio
        }

    }


    fun createComment(postId: String, text: String){

        userData.value?.username?.let{ username ->
            val commentId = UUID.randomUUID().toString()
            val comment = CommentData(
                commentId = commentId,
                postId = postId,
                username = username,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            db.collection(COMMENTS).document(commentId).set(comment)
                .addOnSuccessListener {
                    getComments(postId)
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot create comment")
                }
        }

    }

    fun getComments(postId: String?){

        commentsProgress.value = true
        db.collection(COMMENTS).whereEqualTo("postId", postId).get()

            .addOnSuccessListener { documents ->
                val newComments = mutableListOf<CommentData>()
                documents.forEach { doc ->
                    val comment = doc.toObject<CommentData>()
                    newComments.add(comment)
                }

                val sortedComments = newComments.sortedBy { it.timestamp}
                comments.value = sortedComments
                commentsProgress.value = false
            }

            .addOnFailureListener { exc ->
                handleException(exc, "Cannot retrieve comments")
                commentsProgress.value = false
            }

    }

    private fun getFollowers(uid: String?) {

        db.collection(USERS).whereArrayContains("following", uid ?: "").get()
            .addOnSuccessListener { documents ->
                followers.value = documents.size()
            }

    }

    private val _isDarkTheme = MutableLiveData(true)
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    fun setTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun deletePost(postId: String, navController: NavController) {
        inProgress.value = true

        // Eliminar comentarios relacionados
        db.collection(COMMENTS).whereEqualTo("postId", postId).get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()
                for (document in documents) {
                    batch.delete(document.reference)
                }

                // Eliminar el post en sí
                db.collection(POSTS).document(postId).delete()
                    .addOnSuccessListener {
                        batch.commit().addOnSuccessListener {
                            inProgress.value = false
                            popupNotification.value = Event("Post deleted successfully")
                            refreshPosts {
                                navigateTo(
                                    navController = navController,
                                    dest = DestinationScreen.MyPosts
                                )
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        handleException(e, "Failed to delete post")
                        inProgress.value = false
                    }
            }
            .addOnFailureListener { e ->
                handleException(e, "Failed to delete comments")
                inProgress.value = false
            }
    }



}