package com.example.ui.ads

import android.app.Activity
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed interface ConsentState {
    data object Unknown : ConsentState
    data object Obtained : ConsentState
    data object NotRequired : ConsentState
    data class Error(val message: String) : ConsentState
}

object ConsentManager {

    private var consentInformation: ConsentInformation? = null
    private var consentForm: ConsentForm? = null
    private var _consentState: ConsentState = ConsentState.Unknown
    val consentState: ConsentState get() = _consentState

    suspend fun requestConsent(activity: Activity) {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation?.let { info ->
            suspendCancellableCoroutine<Unit> { continuation ->
                info.requestConsentInfoUpdate(
                    activity,
                    params,
                    {
                        if (info.isConsentFormAvailable) {
                            loadForm(activity) { success ->
                                if (success) {
                                    _consentState = ConsentState.Obtained
                                } else {
                                    _consentState = ConsentState.Error("Failed to load consent form")
                                }
                                continuation.resume(Unit)
                            }
                        } else {
                            _consentState = ConsentState.NotRequired
                            continuation.resume(Unit)
                        }
                    },
                    { _ ->
                        _consentState = ConsentState.Error("Consent info update failed")
                        continuation.resume(Unit)
                    }
                )
            }
        }
    }

    private fun loadForm(activity: Activity, onResult: (Boolean) -> Unit) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { form ->
                consentForm = form
                if (consentInformation?.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    form.show(
                        activity,
                        {
                            onResult(true)
                        }
                    )
                } else {
                    onResult(true)
                }
            },
            { _ ->
                onResult(false)
            }
        )
    }

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(
            activity,
            { }
        )
    }

    fun canShowPrivacyOptions(): Boolean {
        return consentInformation?.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    fun canRequestAds(): Boolean {
        return consentInformation?.canRequestAds() == true
    }

    fun reset() {
        consentInformation?.reset()
        consentForm = null
        _consentState = ConsentState.Unknown
    }
}
