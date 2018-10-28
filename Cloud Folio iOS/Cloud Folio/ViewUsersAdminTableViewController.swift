//
//  ViewUsersAdminTableViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/29/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase


class UserCell: UITableViewCell {
    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var emailLabel: UILabel!
    @IBOutlet var gradeLevelLabel: UILabel!
}

class User {
    var name: String
    var email: String
    var grade: String
    
    init (name: String, email: String, grade: String) {
        self.name = name
        self.email = email
        self.grade = grade
    }
    
    init () {
        self.name = ""
        self.email = ""
        self.grade = ""
    }
    
}

class ViewUsersAdminTableViewController: UITableViewController {

    var dbRef: DatabaseReference = Database.database().reference().child("Users")
    @IBOutlet var userTableView: UITableView!
    var namesList: [String] = [String] ()
    var usersList: [User] = [User] ()
    var selectedName = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return usersList.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! UserCell
        
        cell.nameLabel.text = usersList[indexPath.row].name
        cell.emailLabel.text = usersList[indexPath.row].email
        cell.gradeLevelLabel.text = "Grade Level: " + usersList[indexPath.row].grade

        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedName = namesList[indexPath.row]
        AdminFeedbackViewController.nameOfUser = usersList[indexPath.row].name
        UserInAdminViewController.fullname = usersList[indexPath.row].name
        print (selectedName)
        moveToUserPage()
    }
    
    func fetchData () {
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            for names in snapshot.children {
                self.namesList.append((names as! DataSnapshot).key)
                if self.namesList.count == snapshot.childrenCount {
                    for i in stride(from: 0, to: self.namesList.count, by: 1) {
                        self.dbRef.child(self.namesList[i]).observeSingleEvent(of: .value, with: { (snapshot) in
                            if let val = snapshot.value as? [String: Any?] {
                                var tempUser = User(name: val["FullName"] as! String, email: val["EmailID"] as! String, grade: val["GradeLevel"] as! String)
                                self.usersList.append(tempUser)
                                DispatchQueue.main.async(execute: {
                                    self.userTableView.reloadData()
                                })
                            }
                        })
                    }
                }
            }
        }
    }
    
    func moveToUserPage () {
        UserInAdminViewController.name = selectedName
        self.performSegue(withIdentifier: "MoveToSpecificUser", sender: self)
    }
    
}
