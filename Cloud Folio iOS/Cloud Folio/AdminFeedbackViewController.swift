//
//  AdminFeedbackViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 5/8/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseAuth

class AdminFeedbackViewController: UIViewController {

    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var feedbackLabel: UILabel!
    static var userEmailHeader = ""
    static var nameOfUser = ""
    var dbRef: DatabaseReference = Database.database().reference()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        getData()
        nameLabel.text = AdminFeedbackViewController.nameOfUser
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func getData () {
        Database.database().reference().child("Feedback").child(AdminFeedbackViewController.userEmailHeader).observeSingleEvent(of: .value) { (snapshot) in
            if let val: [String: Any?] = snapshot.value as? [String: Any?] {
                self.feedbackLabel.text = (val["Feedback"] as? String)
            }
        }
    }

}
