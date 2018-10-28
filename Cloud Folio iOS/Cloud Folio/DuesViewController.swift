//
//  DuesViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/28/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseAuth

class DueCell: UITableViewCell {
    @IBOutlet var bookName: UILabel!
    @IBOutlet var amountDueText: UILabel!
    @IBOutlet var numDurationLabel: UILabel!
    
}

class DuesViewController: ViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet var table: UITableView!
    @IBOutlet var totalDueText: UILabel!
    var userEmail: String? = Auth.auth().currentUser?.email?.substring(to: (Auth.auth().currentUser?.email?.index(of: "@"))!)
    var dueBooks: [DueBook] = [DueBook] ()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        getTotalDues()
        getDueBooksValue()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dueBooks.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "dueCell", for: indexPath) as! DueCell
        cell.bookName.text = dueBooks[indexPath.row].book
        cell.amountDueText.text = "Amount: $ " + String(dueBooks[indexPath.row].dueInTotal)
        cell.numDurationLabel.text = "Days since due: " + String(dueBooks[indexPath.row].daysDuration)
        return cell
    }
    
    func getTotalDues () {
        var sum: Double = 0
        var dbRef: DatabaseReference = Database.database().reference().child("Users").child(self.userEmail!).child("Dues")
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            for names in snapshot.children {
                dbRef.child((names as! DataSnapshot).key as! String).child("TotalMoneyDue").observeSingleEvent(of: .value) { (snapshot) in
                    if let val = snapshot.value {
                        sum += (val as? Double)!
                        
                        self.totalDueText.text = "$ " + String (sum)
                        print (sum)
                        
                    }
                }
            }
        }
    }
    
    func getDueBooksValue() {
        var dbRef: DatabaseReference = Database.database().reference().child("Users").child(self.userEmail!).child("Dues")
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            for names in snapshot.children {
                dbRef.child((names as! DataSnapshot).key as! String).observeSingleEvent(of: .value, with: { (snapshot) in
                    if let val = snapshot.value as? [String: Any?] {
                        let tempBook = DueBook(book: (names as! DataSnapshot).key as! String, days: val["DueDuration"] as! Int, due: val["TotalMoneyDue"] as! Double)
                        self.dueBooks.append(tempBook)
                        DispatchQueue.main.async(execute: {
                            self.table.reloadData()
                        })
                    }
                })
            }
        }
    }

}
