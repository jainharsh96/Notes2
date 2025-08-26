package com.notes.shared.coreUi

import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time

actual fun showToast(message: String) {
    val alert = UIAlertController.alertControllerWithTitle(
        title = null,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )
    val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootVC?.presentViewController(alert, animated = true, completion = null)

    // auto-dismiss after 2 sec
    dispatch_after(
        dispatch_time(DISPATCH_TIME_NOW, 2_000_000_000),
        dispatch_get_main_queue()
    ) {
        alert.dismissViewControllerAnimated(true, completion = null)
    }
}