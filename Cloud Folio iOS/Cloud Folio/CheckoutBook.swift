//
//  CheckoutBook.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/21/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import Foundation

class CheckoutBook {
    var name: String
    var checkoutDate: String
    var dateDue: String
    
    init() {
        name = String()
        checkoutDate = String()
        dateDue = String()
    }
    
    init(name: String, checkoutDate: String, dateDue: String) {
        self.name = name
        self.checkoutDate = checkoutDate
        self.dateDue = dateDue
    }
    
    
}
