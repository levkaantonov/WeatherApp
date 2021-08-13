package levkaantonov.com.study.weatherapp.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper {
    companion object {
        fun checkPermissionsIsGrantedAndRequest(
            context: Context,
            permissions: Array<String>,
            requestAction: (permissions: Array<String>) -> Unit
        ) {
            val notGrantedPermissions = mutableListOf<String>()
            for (permission in permissions) {
                if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    notGrantedPermissions.add(permission)
                }
            }
            requestAction(permissions)
        }
    }
}