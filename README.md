# Acceleronix Android App SDK Sample
This sample demonstrates the use of Acceleronix Android App SDK to build an IoT app from scratch. Acceleronix Android App SDK is divided into several functional groups to give developers a clear understanding of different features, including user registration, device binding and control, and device group settings. It supports binding both cellular and WiFi/Bluetooth devices. Device control can be performed via HTTP and WebSocket.

# Get Started
## Initialization
You need to call `QuecSDKMergeManager.getInstance().init(this);` in the `onCreate` method of the application as soon as it starts.

## Configure user domain, user domain secret, and cloud service type

Call `public void initProject(int serviceType, String userDomain, String domainSecret)` in the `onCreate()` method of the application using `QuecSDKMergeManager.getInstance().initProject(0, userDomain, domainSecret);`.

| Parameter    | Required | Description                                           |
|--------------|----------|-------------------------------------------------------|
| serviceType  | Yes      | 0 for domestic, non-0 for international               |
| userDomain   | Yes      | User domain, generated when creating an app on the Acceleronix IoT platform |
| domainSecret | Yes      | User domain secret, generated when creating an app on the Acceleronix IoT platform |

# References
For more information on the Acceleronix Android App SDK, please refer to the [API documentation](https://github.com/Acceleronix/android-sdk-sample/blob/main/API_doc_android.md).
