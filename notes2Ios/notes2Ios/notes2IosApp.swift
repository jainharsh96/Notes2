//
//  notes2IosApp.swift
//  notes2Ios
//
//  Created by Harsh.Jain on 13/02/24.
//

import SwiftUI
import shared


@main
struct notes2IosApp: App {
    init(){
        NotesKoinIOS.shared.doInit()
        }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
