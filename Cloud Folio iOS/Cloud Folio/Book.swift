//
//  Book.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/15/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import Foundation

class Book {
    var authorName: String
    var datePublished: String
    var bookDescription: String
    var bookGenre: String
    var lexileLevel: String
    var bookName: String
    var numPages: String
    var bookStock: String
    var bookType: String
    
    init(authorName: String, datePublished: String, bookDescription: String, bookGenre: String, lexileLevel: String, bookName: String, numPages: String, bookStock: String, bookType: String) {
        self.authorName = authorName
        self.datePublished = datePublished
        self.bookDescription = bookDescription
        self.bookGenre = bookGenre
        self.lexileLevel = lexileLevel
        self.bookName = bookName
        self.numPages = numPages
        self.bookStock = bookStock
        self.bookType = bookType
    }
    
    init () {
        self.authorName = ""
        self.datePublished = ""
        self.bookDescription = ""
        self.bookGenre = ""
        self.lexileLevel = ""
        self.bookName = ""
        self.numPages = ""
        self.bookStock = ""
        self.bookType = ""
    }
    
    func toString () -> String {
        return self.bookName + ":" + self.authorName + ";" + self.bookDescription + ";" + self.bookGenre + ";" + self.lexileLevel + ";" + self.numPages + ";" + self.bookStock + ";" + self.bookType
    }
    
}
