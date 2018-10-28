//
//  UserInAdminViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 5/5/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase

class AdminCell: UITableViewCell {
    @IBOutlet var label: UILabel!
    @IBOutlet var label2: UILabel!
    @IBOutlet var label3: UILabel!
}

class UserInAdminViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    static var name: String = ""
    static var fullname: String = ""
    var segment = 0
    var bookNameIterator: [String] = [String] ()
    var checkoutBooks: [CheckoutBook] = [CheckoutBook] ()
    var dbRef: DatabaseReference = Database.database().reference().child("Users")
    var booknames: [String] = [String] ()
    var holdBooks: [HoldBook] = [HoldBook] ()
    var dueBooks: [DueBook] = [DueBook] ()
    
    @IBOutlet var checkoutTable: UITableView!
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if segment == 0 {
            return checkoutBooks.count
        } else if segment == 1 {
            return holdBooks.count
        } else if segment == 2 {
            return dueBooks.count
        } else {
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as? AdminCell
        if segment == 0 {
            cell?.label.text = checkoutBooks[indexPath.row].name
            cell?.label2.text = "Date Due: " + checkoutBooks[indexPath.row].dateDue
            cell?.label3.text = "Date Checked Out: " + checkoutBooks[indexPath.row].checkoutDate
            return cell!
        } else if segment == 1 {
            cell?.label.text = holdBooks[indexPath.row].bookName
            cell?.label2.text = "Held On: " + holdBooks[indexPath.row].heldOn
            cell?.label3.text = "Checkout Within: " + holdBooks[indexPath.row].checkoutWithin
            return cell!
        } else if segment == 2 {
            cell?.label.text = dueBooks[indexPath.row].book
            cell?.label2.text = "Total Duration Held: " + String(dueBooks[indexPath.row].daysDuration)
            cell?.label3.text = "Total Due: " + String(dueBooks[indexPath.row].dueInTotal)
            return cell!
        }
        else {
            return UITableViewCell()
        }
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = UserInAdminViewController.fullname
        AdminFeedbackViewController.userEmailHeader = UserInAdminViewController.name
        dbRef = dbRef.child(UserInAdminViewController.name).child("Checkout")
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            self.fetchCheckoutData(snapshot: snapshot)
            self.fetchHoldData(snapshot: snapshot)
            self.getDueBooksValue()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func fetchCheckoutData (snapshot: DataSnapshot) {
        bookNameIterator.removeAll()
        checkoutBooks.removeAll()
        for books in snapshot.children {
            self.bookNameIterator.append((books as! DataSnapshot).key)
            print (self.bookNameIterator.count)
            if self.bookNameIterator.count == snapshot.childrenCount {
                for name in self.bookNameIterator {
                    self.dbRef.child(name).observeSingleEvent(of: .value, with: { (snapshot) in
                        if let value = snapshot.value as? [String: Any?] {
                            print (snapshot)
                            var temp: CheckoutBook = CheckoutBook()
                            temp.name = name
                            temp.dateDue = value["DateDue"] as! String
                            temp.checkoutDate = value["CheckoutDate"] as! String
                            self.checkoutBooks.append(temp)
                            DispatchQueue.main.async(execute: {
                                self.checkoutTable.reloadData()
                            })
                        }
                    })
                }
            }
        }
    }
    
    func fetchHoldData (snapshot: DataSnapshot) {
        dbRef = Database.database().reference().child("Users").child(UserInAdminViewController.name)
        self.dbRef.child("Hold").observeSingleEvent(of: .value, with: { (snapshot) in
            
            for books in snapshot.children {
                self.booknames.append(((books as? DataSnapshot)?.key)!)
                if (self.booknames.count == snapshot.childrenCount) {
                    for i in stride(from: 0, to: self.booknames.count, by: 1) {
                        self.dbRef.child("Hold").child(self.booknames[i]).observeSingleEvent(of: .value, with: { (snapshot) in
                            
                            if let val = snapshot.value as? [String: Any?] {
                                
                                var temp = HoldBook(
                                    bookName: self.booknames[i], heldOn: val["HoldDate"] as! String, checkoutWithin: val["CheckoutLimit"] as! String
                                )
                                self.holdBooks.append(temp)
                                DispatchQueue.main.async(execute: {
                                    self.checkoutTable.reloadData()
                                })
                            }
                        })
                    }
                }
            }
        })
    }
    
    func getDueBooksValue() {
        var dbRef: DatabaseReference = Database.database().reference().child("Users").child(UserInAdminViewController.name).child("Dues")
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            for names in snapshot.children {
                dbRef.child((names as! DataSnapshot).key as! String).observeSingleEvent(of: .value, with: { (snapshot) in
                    print (snapshot)
                    if let val = snapshot.value as? [String: Any?] {
                        let tempBook = DueBook(book: (names as! DataSnapshot).key as! String, days: val["DueDuration"] as! Int, due: val["TotalMoneyDue"] as! Double)
                        self.dueBooks.append(tempBook)
                        DispatchQueue.main.async(execute: {
                            self.checkoutTable.reloadData()
                        })
                    }
                })
            }
        }
    }

    @IBAction func onSegmentChange(_ sender: Any) {
        segment = ((sender as? UISegmentedControl)?.selectedSegmentIndex)!
        checkoutTable.reloadData()
    }
}
