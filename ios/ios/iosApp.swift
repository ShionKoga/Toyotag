import SwiftUI
import UserNotifications


@main
struct iosApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @State var user: User?
    
    var body: some Scene {
        WindowGroup {
            ContentView(user: $user)
                .onChange(of: user) { oldValue, newValue in
                    appDelegate.accessToken = newValue?.accessToken
                }
        }
    }
}

struct SaveDeviceTokenRequest: Encodable {
    let deviceToken: String
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    var accessToken: String?
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let deviceToken = tokenParts.joined()
        guard let accessToken = accessToken else { return }
        Task {
            var urlRequest = URLRequest(url: URL(string: "http://localhost:8080/api/notifications/device-token")!)
            urlRequest.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
            urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
            let bodyData = try JSONEncoder().encode(SaveDeviceTokenRequest(deviceToken: deviceToken))
            urlRequest.httpBody = bodyData
            urlRequest.httpMethod = "POST"
            _ = try await URLSession.shared.data(for: urlRequest)
        }
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: any Error) {
        print("Failed to register: \(error.localizedDescription)")
    }
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        return true
    }
    
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable : Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        print("notification received: \(userInfo)")
        if let tagEventId = userInfo["tagEventId"] as? String {
            print("Received tagEventId: \(tagEventId)")
        }
        completionHandler(.newData)
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let userInfo = notification.request.content.userInfo
        print("notification received: \(userInfo)")
        if let tagEventId = userInfo["tagEventId"] as? String {
            print("Received tagEventId while app is in foreground: \(tagEventId)")
        }
        completionHandler([])
    }
}
