//
//  HomeViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/28/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import Firebase
import FirebaseAuth
import FirebaseCore

class DueBook {
    var book: String
    var daysDuration: Int
    var dueInTotal: Double
    
    init(book: String, days: Int, due: Double) {
        self.book = book
        self.daysDuration = days
        self.dueInTotal = due
    }
    init() {
        book = ""
        daysDuration = 0
        dueInTotal = 0.0
    }
}

class HomeViewController: ViewController {

    var userEmail: String? = ""
    var bookNames: [String] = [String] ()
    var dueBooks: [DueBook] = [DueBook] ()
    var bookRefIterator: [String] = []
    var booksArray: [Book] = []
    var dbRef: DatabaseReference = Database.database().reference()
    
    @IBOutlet var randomBookName: UILabel!
    @IBOutlet var randomBookAuthor: UILabel!
    @IBOutlet var randomBookGenre: UILabel!
    @IBOutlet var randomBookLexile: UILabel!
    @IBOutlet var randomBookStock: UILabel!
    
    
    
    @IBAction func onMoreClick(_ sender: Any) {
        BookDetailsViewController.bookName = randomBookName.text!
        performSegue(withIdentifier: "showBookDetails", sender: self)
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        userEmail = Auth.auth().currentUser?.email?.substring(to: (Auth.auth().currentUser?.email?.index(of: "@"))!)
        dbRef = dbRef.child("Users").child(userEmail!).child("Checkout")
        checkDueDate()
        fetchData()
        checkForHoldCheckoutLimit()
    }
    
    override func viewDidAppear(_ animated: Bool) {
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func checkDueDate () {
        
        let dateFormatter: DateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM-dd-yyyy"
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        print ("SQUICK")
        
        dbRef.observe(.value) { (snapshot) in
            print ("SQUICK1")
            self.bookNames.removeAll()
            for names in snapshot.children {
                self.bookNames.append(((names as! DataSnapshot).key))
                print ("SQUICK2")
                print (self.bookNames.count)
                if self.bookNames.count == snapshot.childrenCount {
                    for i in stride(from: 0, to: self.bookNames.count, by: 1) {
                        Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(self.bookNames[i]).observe(.value, with: { (snapshot) in
                            print ("HALLO")
                            if let val = snapshot.value as? [String: Any?] {
                                    print ("ASYNC")
                                if (val["DateDue"] != nil) {
                                    if (dateFormatter.date(from: DateManager().getDate())! > dateFormatter.date(from: val["DateDue"] as! String)!) {
                                        var duration: Int = DateManager().getDuration(date1: dateFormatter.date(from: val["DateDue"] as! String)!, date2: dateFormatter.date(from: DateManager().getDate())!)
                                        print (DateManager().getDuration(date1: dateFormatter.date(from: "4-1-2018")!, date2: dateFormatter.date(from: DateManager().getDate())!))
                                        if (i < self.bookNames.count) {
                                            var tempBook = DueBook(book: self.bookNames[i], days: duration, due: Double(duration) * 0.25)
                                            Database.database().reference().child("Users").child(self.userEmail!).child("Dues").child(tempBook.book).child("DueDuration").setValue(tempBook.daysDuration)
                                            Database.database().reference().child("Users").child(self.userEmail!).child("Dues").child(tempBook.book).child("TotalMoneyDue").setValue(tempBook.dueInTotal)
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }
    
    func fetchData () {
        var dbref = Database.database().reference().child("Books")
        dbref.observeSingleEvent(of: .value) { (snapshot) in
            for books in snapshot.children {
                self.bookRefIterator.append((books as! DataSnapshot).key)
            }
            
            for name in self.bookRefIterator {
                dbref.child(name).observeSingleEvent(of: .value, with: { (snapshot) in
                    var tempBook: Book = Book()
                    if let value = snapshot.value as? [String: Any?] {
                        tempBook.authorName = value["Author"]! as! String
                        tempBook.bookDescription = value["Description"]! as! String
                        tempBook.bookGenre = value["Genre"]! as! String
                        tempBook.lexileLevel = value["Lexile"]! as! String
                        tempBook.bookName = value["Name"]! as! String
                        tempBook.numPages = String(value["Pages"]! as! Int)
                        tempBook.bookStock = String(value["Stock"]! as! Int)
                        tempBook.bookType = value["Type"]! as! String
                        self.booksArray.append(tempBook)
                        
                        DispatchQueue.main.async(execute: {
                            var rand = Int(arc4random_uniform(UInt32(self.booksArray.count)))
                            var book = self.booksArray[rand]
                            self.randomBookName.text = book.bookName
                            self.randomBookAuthor.text = "By " + book.authorName
                            self.randomBookLexile.text = "Lexile: " + book.lexileLevel
                            self.randomBookStock.text = "Stock: " +  book.bookStock
                            self.randomBookGenre.text = book.bookGenre
                        })
                        
                    }
                })
            }
        }
    }
    
    func checkForHoldCheckoutLimit() {
        var books: [String] = [String] ()
        var dbRef = Database.database().reference().child("Users").child(userEmail!).child("Hold")
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            let dateFormatter: DateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM-dd-yyyy"
            for names in snapshot.children {
                books.append((names as! DataSnapshot).key as! String)
                if books.count == snapshot.childrenCount {
                    for bookNames in books {
                        dbRef.child(bookNames).observeSingleEvent(of: .value, with: { (snapshot) in
                            if let val: [String: Any?] = snapshot.value as! [String: Any?] {
                                if (dateFormatter.date(from: DateManager().getDate())! > dateFormatter.date(from: val["CheckoutLimit"] as! String)!){
                                    Database.database().reference().child("Users").child(self.userEmail!).child("Hold").child(bookNames).removeValue()
                                }
                            }
                        })
                    }
                }
            }
        }
    }


}
