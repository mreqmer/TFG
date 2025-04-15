import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthManager {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
    }

    fun logoutWithRevokeAccess(context: Context, onComplete: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("181983126111-1jlv71ep88fv50pbkisj9el1ak734kje.apps.googleusercontent.com") // 👈 usa tu client ID de google-services.json
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        googleSignInClient.revokeAccess().addOnCompleteListener {
            firebaseAuth.signOut()
            onComplete()
        }
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null
}
