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
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world! " + Greeting().greet())
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
