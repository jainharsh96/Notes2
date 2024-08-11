//
//  ContentView.swift
//  notes2Ios
//
//  Created by Harsh.Jain on 13/02/24.
//

import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        ComposeView()
    }
}


struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

#Preview {
    ContentView()
}
