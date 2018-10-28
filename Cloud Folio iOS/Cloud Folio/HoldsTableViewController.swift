//
//  HoldsTableViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/25/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import Firebase
import FirebaseCore
import UIKit
import FirebaseAuth

class HoldsCell: UITableViewCell {
    @IBOutlet var bookname: UILabel!
    @IBOutlet var heldOnDate: UILabel!
    @IBOutlet var inQueueNumLabel: UILabel!
    @IBOutlet var checkoutWithinLabel: UILabel!
}

class HoldBook {
    var bookName: String
    var heldOn: String
    var checkoutWithin: String
    var inQueue: String
    
    init(bookName: String, heldOn: String, checkoutWithin: String) {
        self.bookName = bookName
        self.heldOn = heldOn
        self.checkoutWithin = checkoutWithin
        inQueue = ""
    }
    
}

class HoldsTableViewController: UITableViewController {
    
    var holdBooks: [HoldBook] = [HoldBook] ()
    
    var userEmailUID: String = ""
    var dbRef: DatabaseReference = Database.database().reference()
    var booknames: [String] = [String] ()
    @IBOutlet var holdsTableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        userEmailUID = (Auth.auth().currentUser?.email?.substring(to: (Auth.auth().currentUser?.email?.index(of: "@"))!))!
        dbRef = dbRef.child("Users").child(userEmailUID);
        
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
                                    self.holdsTableView.reloadData()
                                })
                            }
                        })
                    }
                }
            }
        })
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return holdBooks.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! HoldsCell
        cell.bookname.text = holdBooks[indexPath.row].bookName
        cell.checkoutWithinLabel.text = "Pickup within: " + holdBooks[indexPath.row].checkoutWithin
        cell.heldOnDate.text = "Held on: " + holdBooks[indexPath.row].heldOn
        return cell
    }
    
    override func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let pickup = UITableViewRowAction (style: UITableViewRowActionStyle.default, title: "Pickup") { (action, index) in
            self.managePickup(indexPath)
        }
        return [pickup]
    }
    
    func managePickup(_ path: IndexPath) {
        var dbRef: DatabaseReference = Database.database().reference()
        var userID: String = userEmailUID
        var book: String = holdBooks[path.row].bookName
        var dateStringOfUsers: [String] = [String] ()
        var userDate: String = ""
        var nameDatePlaceholderStrings: [String] = [String] ()
        
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            if let val = snapshot.value as? [String: Any?] {
                if val ["HoldQueue"] != nil {
                    dbRef.child("HoldQueue").child(book).observeSingleEvent(of: .value) { (snapshot) in
                        for names in snapshot.children {
                            nameDatePlaceholderStrings.append((names as! DataSnapshot).key)
                        }
                        if let val = snapshot.value as? [String: Any?] {
                            if val[userID] != nil {
                                userDate = val[userID] as! String
                            }
                        }
                        for i in stride(from: 0, to: nameDatePlaceholderStrings.count, by: 1) {
                            dbRef.child("HoldQueue").child(book).child(nameDatePlaceholderStrings[i]).observeSingleEvent(of: .value, with: { (snapshot) in
                                if let val = snapshot.value as? String {
                                    dateStringOfUsers.append(val)
                                }
                                if dateStringOfUsers.count == nameDatePlaceholderStrings.count {
                                    var stockControl = false
                                    var d = DateManager()
                                    if self.canUserPickup(nameDatePlaceholderStrings, dateStringOfUsers, userDate) {
                                        
                                        Database.database().reference().child("Books").child(self.holdBooks[path.row].bookName).child("Stock").observeSingleEvent(of: .value, with: { (snapshot) in
                                            if (!stockControl) {
                                                if let val = snapshot.value {
                                                    if (val as? Int)! >= 1 {
                                                        Database.database().reference().child("Books").child(self.holdBooks[path.row].bookName).child("Stock").setValue((val as? Int)! - 1)
                                                        stockControl = true
                                                        Database.database().reference().child("HoldQueue").child(self.holdBooks[path.row].bookName).child(userID).removeValue()
                                                        Database.database().reference().child("Users").child(userID).child("Hold").child(self.holdBooks[path.row].bookName).removeValue()
                                                        Database.database().reference().child("Users").child(userID).child("Checkout").child(self.holdBooks[path.row].bookName).child("CheckoutDate").setValue(d.getDate())
                                                        Database.database().reference().child("Users").child(userID).child("Checkout").child(self.holdBooks[path.row].bookName).child("DateDue").setValue(d.addDaysToDate(25))
                                                        
                                                        self.navigationController?.popViewController(animated: true)
                                                    } else {
                                                        self.showAlert(headingAlert: "Cannot pickup " + self.holdBooks[path.row].bookName, messageAlert: "Stock is too low. Wait for some more days to pickup.", actionTitle: "Ok", handleAction: { (action) in
                                                            
                                                        })
                                                    }
                                                }
                                            }
                                        })
                                        
                                    } else {
                                        self.showAlert(headingAlert: "Cannot pickup " + self.holdBooks[path.row].bookName, messageAlert: "There is another person waiting for the book. Please wait until the queue reaches you.", actionTitle: "Ok", handleAction: { (action) in
                                            
                                        })
                                    }
                                }
                            })
                        }
                    }
                } else {
                    //hold queue is nil
                }
            }
        }
    }
    
    func canUserPickup (_ userIDS: [String], _ userDates: [String], _ userDateKey: String) -> Bool {
        if userIDS.count == userDates.count {
            var minIndex = 0
            
            print (userDates)
            
            let dateFormatter: DateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM-dd-yyyy"
            dateFormatter.locale = Locale(identifier: "en_US_POSIX")
            let dateKey = dateFormatter.date(from: userDateKey)
            
            for i in stride(from: 0, to: userDates.count, by: 1) {
                let dateCheck1 = dateFormatter.date(from: userDates[i])
                let minDate = dateFormatter.date(from: userDates[minIndex])
                
                print ("Check:  \(dateCheck1)")
                
                if dateCheck1! < minDate! {
                    minIndex = i
                    print ("Less at " + String(i))
                }
                
            }
            
            if dateFormatter.date (from: userDates[minIndex])! >= dateKey! {
                return true
            }
        }
        return false
    }
    
    func showAlert (headingAlert: String, messageAlert: String, actionTitle: String, handleAction: @escaping (_ action: UIAlertAction) -> ()) {
        var alert: UIAlertController = UIAlertController(title: headingAlert, message: messageAlert, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: actionTitle, style: UIAlertActionStyle.default, handler: handleAction))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        BookDetailsViewController.bookName = holdBooks[indexPath.row].bookName
        performSegue(withIdentifier: "fromHoldToDetails", sender: self)
        
    }
    
}
