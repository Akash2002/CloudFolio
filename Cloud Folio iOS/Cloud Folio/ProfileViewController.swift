//
//  ProfileViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/6/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseAuth

class ProfileViewController: UIViewController, UITableViewDelegate, UITableViewDataSource  {
    
    var userFolderOptions: [String] = ["Checkouts","Holds","Bookmarks","Dues"]
    var moveToPageString: String = String()
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell = UITableViewCell(style: UITableViewCellStyle.default, reuseIdentifier: "cell")
        cell.textLabel?.text = userFolderOptions[indexPath.row]
        cell.accessoryType = .disclosureIndicator
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        var row: Int = indexPath.row
        moveToUserFolderPage(moveToPageString: userFolderOptions[row])
    }
    
    @IBOutlet var userFullNameLabel: UILabel!
    @IBOutlet var userEmailID: UILabel!
    @IBOutlet var userSchoolName: UILabel!
    @IBOutlet var userTypeLabel: UILabel!
    @IBOutlet var gradeLevelLabel: UILabel!
    
    var appUser = AppUser()
    var dbRef = Database.database().reference().child("Users") 

    override func viewDidLoad() {
        super.viewDidLoad()
        dbRef = dbRef.child(appUser.getCurrentUserID())
        getUserData()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        print ("Appear")
        getUserData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func getUserData() -> Void {
        dbRef.observeSingleEvent(of: .value, with: { snapshot in
            if let dictValue = snapshot.value as? [String: Any?] {
                self.userFullNameLabel.text = dictValue["FullName"] as? String
                self.userEmailID.text = dictValue["EmailID"] as? String
                self.userTypeLabel.text = dictValue["UserType"] as? String
                self.gradeLevelLabel.text = dictValue["GradeLevel"] as? String
                self.userSchoolName.text = dictValue["School"] as? String
                print (dictValue["FullName"])
            }
        })
    }
   
    @IBAction func signOutUser(_ sender: Any) {
        let firebaseAuth = Auth.auth()
        do {
            try firebaseAuth.signOut()
            var storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
            let StoryBoardViewController: UIViewController = storyBoard.instantiateViewController(withIdentifier: "loginViewController")
            self.present(StoryBoardViewController, animated: true, completion: nil)
        } catch let signOutError as NSError {
            print ("Error signing out: %@", signOutError)
        }
    }
    
    func moveToUserFolderPage (moveToPageString: String) {
        if moveToPageString == "Checkouts" {
            self.performSegue(withIdentifier: "CheckoutsViewControllerSegue", sender: self)
        } else if moveToPageString == "Bookmarks" {
            self.performSegue(withIdentifier: "BookmarksViewSegue", sender: self)
        } else if moveToPageString == "Holds" {
            self.performSegue(withIdentifier: "HoldSegueFromProfile", sender: self)
        } else if moveToPageString == "Dues" {
            self.performSegue(withIdentifier: "toDueSegue", sender: self)
        }
    }
    
    
    
}
