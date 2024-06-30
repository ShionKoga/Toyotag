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

class AppDelegate: NSObject, UIApplicationDelegate {
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
}
