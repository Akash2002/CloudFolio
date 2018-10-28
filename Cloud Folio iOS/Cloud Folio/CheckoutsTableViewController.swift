//
//  CheckoutsTablLeViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/21/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import Firebase
import FirebaseCore

class CheckoutCell: UITableViewCell {
    @IBOutlet var dateDueLabel: UILabel!
    @IBOutlet var dateCheckoutLabel: UILabel!
    @IBOutlet var bookNameLabel: UILabel!
}

class CheckoutsTableViewController: UITableViewController {
    
    
    @IBOutlet var checkoutTableView: UITableView!
    
    var userEmail: String? = ""
    var dbRef: DatabaseReference = Database.database().reference().child("Users")
    var bookNameIterator: [String] = [String] ()
    var checkoutBooks: [CheckoutBook] = [CheckoutBook] ()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        userEmail = Auth.auth().currentUser?.email?.substring(to: (Auth.auth().currentUser?.email?.index(of: "@"))!)
        dbRef = dbRef.child(userEmail!).child("Checkout")
        fetchData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return checkoutBooks.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as? CheckoutCell
        cell?.bookNameLabel.text = checkoutBooks[indexPath.row].name
        cell?.dateDueLabel.text = "Date Due: " + checkoutBooks[indexPath.row].dateDue
        cell?.dateCheckoutLabel.text = "Date Checked Out: " + checkoutBooks[indexPath.row].checkoutDate
        return cell!
    }
    
    override func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        var stockControl = false
        
        let returnBooks = UITableViewRowAction(style: UITableViewRowActionStyle.default, title: "Return") { (action, path) in
            Database.database().reference().child("Books").child(self.checkoutBooks[indexPath.row].name).child("Stock").observeSingleEvent(of: .value, with: { (snapshot) in
                if (!stockControl) {
                    if let val = snapshot.value {
                        print (val)
                        Database.database().reference().child("Books").child(self.checkoutBooks[indexPath.row].name).child("Stock").setValue((val as? Int)!  + 1)
                        print ("Done")
                        stockControl = true
                    }
                }
            })
            Database.database().reference().child("Users").child(self.userEmail!).child("Checkout").child(self.checkoutBooks[path.row].name).removeValue()
            self.navigationController?.popViewController(animated: true)
        }
        returnBooks.backgroundColor = UIColor.blue
        
        return [returnBooks]
    }
    
    public func fetchData () {
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            self.checkoutBooks.removeAll()
            self.bookNameIterator.removeAll()
            print ("WHY???!!!!")
            for books in snapshot.children {
                print(books)
                self.bookNameIterator.append((books as! DataSnapshot).key)
                print (self.bookNameIterator.count)
                if self.bookNameIterator.count == snapshot.childrenCount {
                    for name in self.bookNameIterator {
                        var handle = self.dbRef.child(name).observeSingleEvent(of: .value, with: { (snapshot) in
                            if let value = snapshot.value as? [String: Any?] {
                                print (value)
                                var temp: CheckoutBook = CheckoutBook()
                                temp.name = name
                                if (value["DateDue"] != nil) {
                                    temp.dateDue = value["DateDue"] as! String
                                }
                                if (value["CheckoutDate"] != nil) {
                                    temp.checkoutDate = value["CheckoutDate"] as! String
                                }
                                if (self.checkoutBooks.count < self.bookNameIterator.count) {
                                    self.checkoutBooks.append(temp)
                                }
                                DispatchQueue.main.async(execute: {
                                    self.checkoutTableView.reloadData()
                                })
                            }
                        })
                    }
                }
            }
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        BookDetailsViewController.bookName = checkoutBooks[indexPath.row].name
        performSegue(withIdentifier: "fromCheckoutToDetails", sender: self)
    }
}
