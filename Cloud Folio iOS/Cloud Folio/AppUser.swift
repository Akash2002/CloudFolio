//
//  AppUser.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/6/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import Foundation
import FirebaseAuth

class AppUser {
    private var currentUserEmail = Auth.auth().currentUser?.email
    
    init () {
        
    }
    
    func getCurrentUserID () -> String {
        var index: String.Index = (self.currentUserEmail?.index(of: "@")!)!
        return String(currentUserEmail![..<index])
    }
    
}
