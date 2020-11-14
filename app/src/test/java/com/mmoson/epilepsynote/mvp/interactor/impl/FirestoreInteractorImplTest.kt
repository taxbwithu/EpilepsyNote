package com.mmoson9.epilepsynote.mvp.interactor.impl

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class FirestoreInteractorImplTest {
    @JvmField
    @Rule
    var mockitoRule = MockitoJUnit.rule()

    lateinit var firestoreInteractor: FirestoreInteractorImpl

    lateinit var findSeizuresListener: FirestoreInteractorImpl.OnSuccessfullyRecievedSeizures

    lateinit var findSingleSeizureListener: FirestoreInteractorImpl.OnSuccesfullyReceivedSingleSeizure

    @Mock
    val db = mock(FirebaseFirestore::class.java)

    @Mock
    val usersRef = mock(CollectionReference::class.java)

    @Mock
    val onUserIDRequest = mock(FirestoreInteractor.onUserIDRequest::class.java)

    @Mock
    val onActionDone = mock(FirestoreInteractor.onActionDone::class.java)

    @Mock
    val onListRequest = mock(FirestoreInteractor.onSeizureListRequest::class.java)

    @Mock
    val onSeizureRequest = mock(FirestoreInteractor.onSeizureRequest::class.java)

    @Mock
    val querySnap = mock(QuerySnapshot::class.java)

    @Mock
    val seizure = mock(Seizure::class.java)

    @Mock
    val docRef = mock(DocumentReference::class.java)

    @Mock
    val colRef = mock(CollectionReference::class.java)

    @Mock
    val queryDirections = mock(Query::class.java)

    @Mock
    lateinit var document: MutableIterator<QueryDocumentSnapshot>

    @Mock
    lateinit var firestoreDocumentRefResult: Task<DocumentReference>

    @Mock
    lateinit var firestoreDocumentRefSnapshot: Task<DocumentSnapshot>

    @Mock
    lateinit var taskDone : Task<Void>

    @Mock
    lateinit var firestoreQueryResult: Task<QuerySnapshot>

    @Mock
    var seizures = ArrayList<Seizure>()

    @Before
    fun setUp() {
        for (i in 0..3) {
            seizures.add(seizure)
        }
        firestoreInteractor = FirestoreInteractorImpl(db, usersRef)
        findSeizuresListener =
            firestoreInteractor.OnSuccessfullyRecievedSeizures(seizures, onListRequest)
        findSingleSeizureListener =
            firestoreInteractor.OnSuccesfullyReceivedSingleSeizure(onSeizureRequest)
    }

    @Test
    fun testInitUser() {
        `when`(usersRef.document()).thenReturn(docRef)
        `when`(docRef.id).thenReturn("test")
        firestoreInteractor.initUser(onUserIDRequest)
        verify(onUserIDRequest, Mockito.times(1)).onUserIDReturn("test")
    }

    @Test
    fun testAddSeizureWhenUserEmpty() {
        firestoreInteractor.addSeizure(
            "", seizure,
            "1", onUserIDRequest, onActionDone
        )
        verify(onActionDone, Mockito.times(1))
            .onActionDone(false)
    }

    @Test
    fun testAddSeizureWhenUserNotEmpty() {
        `when`(usersRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.collection(anyString())).thenReturn(colRef)
        `when`(colRef.add(seizure)).thenReturn(firestoreDocumentRefResult)
        firestoreInteractor.addSeizure(
            "test", seizure,
            "2", onUserIDRequest, onActionDone
        )
        verify(onActionDone, Mockito.times(1))
            .onActionDone(true)
    }

    @Test
    fun testGetSeizures() {
        `when`(usersRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.collection(anyString())).thenReturn(colRef)
        `when`(
            colRef.orderBy(
                anyString(),
                Mockito.eq(Query.Direction.DESCENDING)
            )
        ).thenReturn(queryDirections)
        `when`(queryDirections.get()).thenReturn(firestoreQueryResult)
        firestoreInteractor.getSeizures("test", onListRequest)
    }

    @Test
    fun testGetSingleSeizure() {
        `when`(usersRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.collection(anyString())).thenReturn(colRef)
        `when`(colRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.get()).thenReturn(firestoreDocumentRefSnapshot)
        firestoreInteractor.getSingleSeizure("test", "test", onSeizureRequest)
    }

    @Test
    fun testUpdateSeizure() {
        `when`(usersRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.collection(anyString())).thenReturn(colRef)
        `when`(colRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.set(seizure)).thenReturn(taskDone)
        firestoreInteractor.updateSeizure("test", "test", seizure, onActionDone)
        verify(onActionDone, times(1))
            .onActionDone(true)
    }

    @Test
    fun testDeleteSeizure() {
        `when`(usersRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.collection(anyString())).thenReturn(colRef)
        `when`(colRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.delete()).thenReturn(taskDone)
        firestoreInteractor.deleteSeizure("test", "test", onActionDone)
        verify(onActionDone, times(1))
            .onActionDone(false)
    }

    @Test
    fun testDeleteUser() {
        `when`(usersRef.document(anyString())).thenReturn(docRef)
        `when`(docRef.delete()).thenReturn(taskDone)
        firestoreInteractor.deleteUser("test", onActionDone)
        verify(onActionDone, times(1))
            .onActionDone(true)
    }

    @Test
    fun testReceiveSeizuresListener_OnSuccessfullyRecievedSeizures(){
        `when`(querySnap.iterator()).thenReturn(document)
        findSeizuresListener.onSuccess(querySnap)
        verify(onListRequest, times(1)).onSeizureListReturn(seizures)
    }

    @Test
    fun testReceiveSingleSeizureListener_OnSuccesfullyReceivedSingleSeizure(){
        val docSnap = mock(DocumentSnapshot::class.java)
        `when`(docSnap.toObject(Seizure::class.java)).thenReturn(seizure)
        findSingleSeizureListener.onSuccess(docSnap)
        verify(onSeizureRequest, times(1)).onSeizureReturn(seizure)
    }
}