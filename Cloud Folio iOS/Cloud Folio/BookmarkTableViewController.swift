//
//  BookmarkTableViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/21/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import Firebase
import FirebaseDatabase

class BookmarkTableViewController: UITableViewController {
    var bookmarksList: [String] = [String] ()
    var stockList: [String] = [String] ()
    
    var count: Int = 0
    @IBOutlet var bookmarkTableView: UITableView!
    
    var userEmail: String? = Auth.auth().currentUser?.email?.substring(to: (Auth.auth().currentUser?.email?.index(of: "@"))!)
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        fetchData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return bookmarksList.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: UITableViewCellStyle.default, reuseIdentifier: "cell")
        cell.textLabel?.text = bookmarksList[indexPath.row]
        return cell
    }
 
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == UITableViewCellEditingStyle.delete { Database.database().reference().child("Users").child(userEmail!).child("Bookmarks").child(bookmarksList[indexPath.row]).removeValue()
            bookmarksList.remove(at: indexPath.row)
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        BookDetailsViewController.bookName = bookmarksList[indexPath.row]
        performSegue(withIdentifier: "fromBookmarkToDetails", sender: self)
    }
    
    func fetchData () {
        var c = 0;
        bookmarksList.removeAll()
Database.database().reference().child("Users").child(userEmail!).child("Bookmarks").observeSingleEvent(of: .value, with: { (snapshot) in
            for children in snapshot.children {
                self.bookmarksList.append((children as! DataSnapshot).key)
                print (self.bookmarksList[c])
                c += 1
                DispatchQueue.main.async(execute: {
                    self.bookmarkTableView.reloadData()
                })
                print (children)
            }
        }, withCancel: nil)
        
    }

    
    
}
