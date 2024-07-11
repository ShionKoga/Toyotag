import Foundation
import Combine


protocol Subscribable {
    associatedtype Output
    
    func subscribe(receiveValue: @escaping ((Self.Output) -> Void)) -> AnyCancellable
}

extension NotificationCenter.Publisher: Subscribable {
    func subscribe(receiveValue: @escaping ((Notification) -> Void)) -> AnyCancellable {
        return self.sink(receiveValue: receiveValue)
    }
}
