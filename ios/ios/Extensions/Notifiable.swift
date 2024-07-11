import Foundation
import Combine


protocol Notifiable {
    func post(name: Notification.Name, object: Any?)
    func publisher(for name: Notification.Name, object: AnyObject?) -> any Subscribable
}

extension NotificationCenter: Notifiable {
    func publisher(for name: Notification.Name, object: AnyObject?) -> any Subscribable {
        return self.publisher(for: name)
    }
}

extension Notification.Name {
    static let tagEventReceive = Notification.Name("tagEventReceive")
}

