import SwiftUI
import AuthenticationServices

struct ContentView: View {
    @Binding var user: User?
    
    var body: some View {
        if let user = user {
            MainScreen(user: user)
                .onAppear {
                    checkNotificationAuthorization()
                }
        } else {
            LoginScreen(user: $user)
        }
    }
    
    private func checkNotificationAuthorization() {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .notDetermined:
                requestNotificationAuthorization()
            case .denied:
                print("Notification permission denied")
            case .authorized:
                print("Notification permission granted")
            default:
                break
            }
        }
    }
    
    private func requestNotificationAuthorization() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Authorization error: \(error.localizedDescription)")
            } else if granted {
                print("Notification permission granted")
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            } else {
                print("Notification permission denied")
            }
        }
    }
}

struct MainScreen: View {
    let user: User
    var body: some View {
        Text(user.email)
    }
}

struct LoginScreen: View {
    @Binding var user: User?
    
    var body: some View {
        VStack {
            Text("Login").font(.title)
            
            SignInWithAppleButton(.signIn) { request in
                request.requestedScopes = [.fullName, .email]
            } onCompletion: { result in
                switch result {
                case .success(let authResults):
                    switch authResults.credential {
                    case let appleIDCredential as ASAuthorizationAppleIDCredential:
                        guard let idToken = appleIDCredential.identityToken else { return }
                        guard let idTokenString = String(data: idToken, encoding: .utf8) else { return }
                        getMe(with: idTokenString)
                    default:
                        break
                    }
                case .failure(let error):
                    print("Authorization failed: " + error.localizedDescription)
                }
            }
        }
    }
    
    private func getMe(with idToken: String) {
        Task {
            var urlRequest = URLRequest(url: URL(string: "http://localhost:8080/auth/user/me")!)
            urlRequest.setValue("Bearer \(idToken)", forHTTPHeaderField: "Authorization")
            let (data, _) = try await URLSession.shared.data(for: urlRequest)
            let user = try JSONDecoder().decode(User.self, from: data)
            self.user = user
        }
    }
}

struct User: Decodable, Equatable {
    let email: String
    let accessToken: String
}
