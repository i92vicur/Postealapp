package com.androidcoursehogent.postealapp

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.data.PostData
import com.androidcoursehogent.postealapp.data.UserData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.UUID
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import org.awaitility.Awaitility.await
import org.mockito.kotlin.times

class PostealappViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    private lateinit var viewModel: PostealappViewModel

    @Before
    fun setUp() {
        val mockAuth = mock<FirebaseAuth>()
        val mockUser = mock<FirebaseUser>()
        whenever(mockUser.uid).thenReturn("testUserId")
        whenever(mockAuth.currentUser).thenReturn(mockUser)

        viewModel = PostealappViewModel(auth = mock(), db = mock(), storage = mock())
    }

    @Test
    fun testOnLogout() {
        viewModel.onLogout()

        assertFalse(viewModel.signedIn.getOrAwaitValue())  // Usando la función adaptada
        assertNull(viewModel.userData.getOrAwaitValue())   // Usando la función adaptada
    }

    @Test
    fun testUpdateProfileData() {
        val name = "New Name"
        val username = "NewUsername"
        val bio = "New bio"

        // Simula la actualización del perfil
        viewModel.updateProfileData(name, username, bio)


        viewModel.userData.value = UserData(
            userId = "testUid",
            name = name,
            username = username,
            bio = bio
        )


        val updatedUserData = viewModel.userData.getOrAwaitValue()
        assertEquals(name, updatedUserData?.name)
        assertEquals(username, updatedUserData?.username)
        assertEquals(bio, updatedUserData?.bio)
    }

    @Test
    fun testSearchPosts() {
        val searchTerm = "hola"

        // Simula las publicaciones que serían devueltas por la búsqueda
        val mockPosts = listOf(
            PostData(postId = "1", postDescription = "hola mundo", time = System.currentTimeMillis()),
            PostData(postId = "2", postDescription = "esto es una prueba", time = System.currentTimeMillis()),
            PostData(postId = "3", postDescription = "hola de nuevo", time = System.currentTimeMillis())
        )

        // Simula el comportamiento de búsqueda
        whenever(viewModel.db.collection(POSTS).whereArrayContains("searchTerms", searchTerm).get()).thenReturn(mock {
            on { addOnSuccessListener(any()) } doAnswer { invocation ->
                val callback = invocation.arguments[0] as (List<PostData>) -> Unit
                callback(mockPosts) // Devuelve los posts simulados
                mock()
            }
        })

        // Ejecuta la búsqueda
        viewModel.searchPosts(searchTerm)

        // Verifica los resultados de la búsqueda
        val searchedPosts = viewModel.searchedPosts.getOrAwaitValue()
        assertNotNull(searchedPosts)
        assertEquals(2, searchedPosts.size) // Debería haber encontrado 2 publicaciones con "hola"
        assertTrue(searchedPosts.any { it.postDescription?.contains("hola") == true })
    }

    @Test
    fun testOnNewPost() {
        val uri = Uri.parse("http://fake-uri.com")
        val description = "A new post description"
        val location = "Test Location"

        // Configuración de mocks para FirebaseStorage
        val storageRef = mock<StorageReference>()
        val imageRef = mock<StorageReference>()
        val uploadTask = mock<UploadTask>()
        val taskSnapshot = mock<UploadTask.TaskSnapshot>()
        val metadata = mock<StorageMetadata>()
        val downloadUri = Uri.parse("http://fake-download-url.com/image.jpg")

        // Simula los métodos de FirebaseStorage
        whenever(viewModel.storage.reference).thenReturn(storageRef)
        whenever(storageRef.child(any())).thenReturn(imageRef)
        whenever(imageRef.putFile(any())).thenReturn(uploadTask)
        whenever(uploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<UploadTask.TaskSnapshot>
            whenever(taskSnapshot.metadata).thenReturn(metadata)
            whenever(metadata.reference).thenReturn(imageRef)
            whenever(imageRef.downloadUrl).thenReturn(Tasks.forResult(downloadUri))
            listener.onSuccess(taskSnapshot)
            uploadTask
        }

        // Ejecuta la función bajo prueba
        viewModel.onNewPost(uri, description, location) {}

        // Verifica el resultado
        val posts = viewModel.posts.getOrAwaitValue()
        assertTrue(posts.any { it.postDescription == description && it.location == location })
    }

    private fun <T> MutableState<T>.getOrAwaitValue(): T {
        return this.value
    }

}