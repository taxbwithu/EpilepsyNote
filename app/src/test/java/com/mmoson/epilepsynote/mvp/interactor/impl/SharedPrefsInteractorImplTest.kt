package com.mmoson9.epilepsynote.mvp.interactor.impl

import android.content.Context
import android.content.SharedPreferences
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class SharedPrefsInteractorImplTest {
    @JvmField
    @Rule
    var mockitoRule = MockitoJUnit.rule()

    lateinit var sharedPrefsInteractor: SharedPrefsInteractorImpl

    @Mock
    val context = mock(Context::class.java)

    @Mock
    val onIDRequest = mock(SharedPrefsInteractor.onIDRequest::class.java)

    @Mock
    val onSettingsRequest = mock(SharedPrefsInteractor.onSettingsRequest::class.java)

    @Mock
    val onActionDone = mock(SharedPrefsInteractor.onActionDone::class.java)

    @Mock
    val sharedPrefs = mock(SharedPreferences::class.java)

    @Mock
    val editor = mock(SharedPreferences.Editor::class.java)

    @Before
    fun setUp() {
        sharedPrefsInteractor = SharedPrefsInteractorImpl(context)
    }

    @Test
    fun testAccessSharedPrefs() {
        `when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(
            sharedPrefs
        )
        sharedPrefsInteractor.accessSharedPrefs()
    }

    @Test
    fun testCheckUserID() {
        `when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(
            sharedPrefs
        )
        `when`(
            sharedPrefs.getString(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn("")
        sharedPrefsInteractor.checkUserID(onIDRequest)
        verify(onIDRequest, times(1)).onSharedPrefsIDReturn("")
    }

    @Test
    fun testSetUserID() {
        `when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(
            sharedPrefs
        )
        `when`(sharedPrefs.edit()).thenReturn(editor)
        sharedPrefsInteractor.setUserID("id")
    }

    @Test
    fun testGetCurrentSettings() {
        `when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(
            sharedPrefs
        )
        `when`(
            sharedPrefs.getString(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn("")
        sharedPrefsInteractor.getCurrentSettings(onSettingsRequest)
        verify(onSettingsRequest, times(1)).onSettingsReturn("", "")
    }

    @Test
    fun testChangeSettings() {
        Mockito.`when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(
            sharedPrefs
        )
        Mockito.`when`(sharedPrefs.edit()).thenReturn(editor)
        sharedPrefsInteractor.changeSettings("sign", "message", onActionDone)
        verify(onActionDone, times(1)).onActionDone()
    }
}