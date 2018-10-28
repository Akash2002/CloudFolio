//
//  BookDetailsViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/20/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseCore
import Firebase

class BookDetailsViewController: UIViewController {
    static var bookName: String = ""
    
    var bookRefIterator = [String] ()
    var tempBook: Book = Book()
    var dbRef: DatabaseReference = Database.database().reference()
    var dateManager: DateManager = DateManager()
    
    var userEmail: String? = Auth.auth().currentUser?.email?.substring(to: (Auth.auth().currentUser?.email?.index(of: "@"))!)
    
    @IBOutlet var checkoutButton: UIButton!
    @IBOutlet var holdButton: UIButton!
    
    @IBOutlet var booknameLabel: UILabel!
    @IBOutlet var lexileNameLabel: UILabel!
    @IBOutlet var authorNameLexile: UILabel!
    @IBOutlet var genreBookLabel: UILabel!
    @IBOutlet var numBookPagesLabel: UILabel!
    @IBOutlet var descriptionLabel: UILabel!
    @IBOutlet var stockLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupButtons()
        dbRef = dbRef.child("Books").child(BookDetailsViewController.bookName)
        fetchData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func setupButtons () {
        checkoutButton.layer.borderWidth = 1.5
        checkoutButton.layer.borderColor = (self.view.tintColor as! UIColor).cgColor
        checkoutButton.layer.cornerRadius = 10
        
        holdButton.layer.borderWidth = 1.5
        holdButton.layer.borderColor = (self.view.tintColor as! UIColor).cgColor
        holdButton.layer.cornerRadius = 10
    }
    
    @IBAction func onCheckoutClick(_ sender: Any) {
        
        var dbRef: DatabaseReference = Database.database().reference().child("Users").child(userEmail!)
        var tempPrevBooksCheckedOut: [String] = [String] ()
        var tempPrevHeldBooks: [String] = [String] ()
        var i = 0
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            if let val = snapshot.value as? [String:Any?] {
                if val["Checkout"] != nil {
                    dbRef.child("Checkout").observeSingleEvent(of: .value, with: { (snapshot) in
                        for v in snapshot.children {
                            tempPrevBooksCheckedOut.append((v as! DataSnapshot).key)
                            i += 1
                            if (tempPrevBooksCheckedOut.count == snapshot.childrenCount) {
                                for j in stride(from: 0, to: tempPrevBooksCheckedOut.count, by: 1) {
                                    if tempPrevBooksCheckedOut[j] != BookDetailsViewController.bookName {
                                        if val["Hold"] != nil {
                                        dbRef.child("Hold").observeSingleEvent(of: .value, with: { (snapshot) in
                                            var j = 0
                                            for val in snapshot.children {
                                                tempPrevHeldBooks.append((val as! DataSnapshot).key)
                                                print(tempPrevHeldBooks.count)
                                                if tempPrevHeldBooks[j] != BookDetailsViewController.bookName {
                                                    if (tempPrevHeldBooks.count / tempPrevBooksCheckedOut.count == snapshot.childrenCount) {
                                                        Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").observeSingleEvent(of: .value, with: { (snapshot) in
                                                            if let val = snapshot.value {
                                                                if ((val as? Int)! > 0) {
                                                                    var dateCheckedOut = self.dateManager.getDate()
                                                                    var dueDate = self.dateManager.addDaysToDate(25)
                                                                    var stock: Int = Int()
                                                                    var stockValChanged = false
                                                                    Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(BookDetailsViewController.bookName).child("CheckoutDate").setValue(dateCheckedOut)
                                                                    Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(BookDetailsViewController.bookName).child("DateDue").setValue(dueDate)
                                                                    
                                                                    Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").observeSingleEvent(of: .value, with: { (snapshot) in
                                                                        print ("IN3")
                                                                        if let value = snapshot.value as? Any? {
                                                                            stock = (value as? Int)!
                                                                        }
                                                                        if stock > 0 {
                                                                            if !stockValChanged {
                                                                                Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").setValue(stock - 1)
                                                                                stockValChanged = true
                                                                                self.navigationController?.popViewController(animated: true)
                                                                            }
                                                                        }
                                                                        
                                                                    }, withCancel: nil)
                                                                } else {
                                                                    self.showAlert(headingAlert: "Cannot checkout " + BookDetailsViewController.bookName, messageAlert: "Stock is too low. Hold book instead.", actionTitle: "Hold", handleAction: { (action) in
                                                                        self.holdBook()
                                                                    })
                                                                }
                                                            }
                                                        }, withCancel: nil)
                                                    }
                                                } else {
                                                    self.showAlert(headingAlert: "Cannot checkout " + BookDetailsViewController.bookName, messageAlert: "This book has been held previously.", actionTitle: "Ok", handleAction: { (action) in
                                                        
                                                    })
                                                }
                                                j += 1
                                            }
                                        }, withCancel: nil)
                                        } else {
                                            var dateCheckedOut = self.dateManager.getDate()
                                            var dueDate = self.dateManager.addDaysToDate(25)
                                            var stock: Int = Int()
                                            var stockValChanged = false
                                            Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(BookDetailsViewController.bookName).child("CheckoutDate").setValue(dateCheckedOut)
                                            Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(BookDetailsViewController.bookName).child("DateDue").setValue(dueDate)
                                            
                                            Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").observeSingleEvent(of: .value, with: { (snapshot) in
                                                if let value = snapshot.value as? Any? {
                                                    stock = (value as? Int)!
                                                }
                                                if stock > 0 {
                                                    if !stockValChanged {
                                                        Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").setValue(stock - 1)
                                                        stockValChanged = true
                                                        self.navigationController?.popViewController(animated: true)
                                                    }
                                                }
                                                
                                            }, withCancel: nil)
                                        }
                                    } else {
                                        self.showAlert(headingAlert: "Cannot checkout " + BookDetailsViewController.bookName, messageAlert: "This book has been checked out previously.", actionTitle: "Ok", handleAction: { (action) in
                                            
                                        })
                                    }
                                }
                            }
                            
                        }
                    })
                } else {
                    var dateCheckedOut = self.dateManager.getDate()
                    var dueDate = self.dateManager.addDaysToDate(25)
                    var stock: Int = Int()
                    var stockValChanged = false
                    Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(BookDetailsViewController.bookName).child("CheckoutDate").setValue(dateCheckedOut)
                    Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(BookDetailsViewController.bookName).child("DateDue").setValue(dueDate)
                    
                    Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").observeSingleEvent(of: .value, with: { (snapshot) in
                        if let value = snapshot.value as? Any? {
                            stock = (value as? Int)!
                        }
                        if stock > 0 {
                            if !stockValChanged {
                                Database.database().reference().child("Books").child(BookDetailsViewController.bookName).child("Stock").setValue(stock - 1)
                                stockValChanged = true
                                self.navigationController?.popViewController(animated: true)
                            }
                        }
                        
                    }, withCancel: nil)
                }
            }
        }
    }
    
    @IBAction func onHoldClick(_ sender: Any) {
        holdBook()
    }
    
    @IBAction func onBookmarkClick(_ sender: Any) {
        Database.database().reference().child("Users").child(userEmail!).child("Bookmarks").child(BookDetailsViewController.bookName).setValue(0)
        
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onShareClick(_ sender: Any) {
        let shareController = UIActivityViewController(activityItems: ["Read " + BookDetailsViewController.bookName + " from CloudFolio: your Library on the Cloud!"], applicationActivities: nil)
        shareController.popoverPresentationController?.sourceView = self.view
        self.present(shareController, animated: true, completion: nil)
    }
    
    func fetchData () {
        self.dbRef.observeSingleEvent(of: .value, with: { (snapshot) in
            if let value = snapshot.value as? [String: Any?] {
                self.tempBook.authorName = value["Author"]! as! String
                self.tempBook.bookDescription = value["Description"]! as! String
                self.tempBook.bookGenre = value["Genre"]! as! String
                self.tempBook.lexileLevel = value["Lexile"]! as! String
                self.tempBook.bookName = value["Name"]! as! String
                self.tempBook.numPages = String(value["Pages"]! as! Int)
                self.tempBook.bookStock = String(value["Stock"]! as! Int)
                self.tempBook.bookType = value["Type"]! as! String
                self.authorNameLexile.text = "by " + self.tempBook.authorName
                self.booknameLabel.text = self.tempBook.bookName
                self.lexileNameLabel.text = String(self.tempBook.lexileLevel)
                self.descriptionLabel.text = self.tempBook.bookDescription
                self.genreBookLabel.text = self.tempBook.bookGenre + " " + self.tempBook.bookType
                self.numBookPagesLabel.text = self.tempBook.numPages + " pages"
                self.stockLabel.text = self.tempBook.bookStock
            }
        })
    }
   
    func showAlert (headingAlert: String, messageAlert: String, actionTitle: String, handleAction: @escaping (_ action: UIAlertAction) -> ()) {
        var alert: UIAlertController = UIAlertController(title: headingAlert, message: messageAlert, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: actionTitle, style: UIAlertActionStyle.default, handler: handleAction))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    func managePickupHoldQueue(date: String) {
        var dbRef: DatabaseReference = Database.database().reference().child("HoldQueue")
        dbRef.child(BookDetailsViewController.bookName).child(userEmail!).setValue(date)
    }
    
    func holdBook (){
        var dbRef: DatabaseReference = Database.database().reference().child("Users").child(userEmail!)
        var tempPrevBooksCheckedOut: [String] = [String] ()
        var tempPrevHeldBooks: [String] = [String] ()
        var i = 0
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            if let val = snapshot.value as? [String: Any?] {
                if val["Hold"] != nil && val["Checkout"] != nil{
                    dbRef.child("Checkout").observeSingleEvent(of: .value, with: { (snapshot) in
                        for v in snapshot.children {
                            tempPrevBooksCheckedOut.append((v as! DataSnapshot).key)
                            i += 1
                            if (tempPrevBooksCheckedOut.count == snapshot.childrenCount) {
                                for j in stride(from: 0, to: tempPrevBooksCheckedOut.count, by: 1) {
                                    if tempPrevBooksCheckedOut[j] != BookDetailsViewController.bookName {
                                        dbRef.child("Hold").observeSingleEvent(of: .value, with: { (snapshot) in
                                            var j = 0
                                            for val in snapshot.children {
                                                tempPrevHeldBooks.append((val as! DataSnapshot).key)
                                                if tempPrevHeldBooks[j] != BookDetailsViewController.bookName {
                                                    if (tempPrevHeldBooks.count / tempPrevBooksCheckedOut.count == snapshot.childrenCount) {
                                                        var dateHeld =  self.dateManager.getDate()
                                                        var checkoutLimitDate = self.dateManager.addDaysToDate(25)
                                                        var stock: Int = Int()
                                                        var stockValChanged = false
                                                        
                                                        self.managePickupHoldQueue(date: dateHeld)
                                                        Database.database().reference().child("Users").child(self.userEmail!).child("Hold").child(BookDetailsViewController.bookName).child("HoldDate").setValue(dateHeld)
                                                        Database.database().reference().child("Users").child(self.userEmail!).child("Hold").child(BookDetailsViewController.bookName).child("CheckoutLimit").setValue(checkoutLimitDate)
                                                        self.navigationController?.popViewController(animated: true)
                                                    }
                                                } else {
                                                    self.showAlert(headingAlert: "Cannot checkout " + BookDetailsViewController.bookName, messageAlert: "This book has been held previously.", actionTitle: "Ok", handleAction: { (action) in
                                                        
                                                    })
                                                }
                                                j += 1
                                            }
                                        }, withCancel: nil)
                                    } else {
                                        self.showAlert(headingAlert: "Cannot checkout " + BookDetailsViewController.bookName, messageAlert: "This book has been checked out previously.", actionTitle: "Ok", handleAction: { (action) in
                                            
                                        })
                                    }
                                }
                            }
                            
                        }
                    })
                } else {
                    var dateHeld =  self.dateManager.getDate()
                    var checkoutLimitDate = self.dateManager.addDaysToDate(25)
                    var stock: Int = Int()
                    var stockValChanged = false
                    
                    self.managePickupHoldQueue(date: dateHeld);
                    Database.database().reference().child("Users").child(self.userEmail!).child("Hold").child(BookDetailsViewController.bookName).child("HoldDate").setValue(dateHeld)
                    Database.database().reference().child("Users").child(self.userEmail!).child("Hold").child(BookDetailsViewController.bookName).child("CheckoutLimit").setValue(checkoutLimitDate)
                    self.navigationController?.popViewController(animated: true)
                }
            }
        }
    }
}
