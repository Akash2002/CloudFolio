//
//  FeedbackViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/6/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase

class FeedbackViewController: UIViewController {

    @IBOutlet var feedbackInput: UITextView!
    var dbRef: DatabaseReference = Database.database().reference().child("Feedback").child(AppUser().getCurrentUserID())
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func sendFeedback(_ sender: Any) {
        dbRef.child("Feedback").setValue(feedbackInput.text)
        performSegue(withIdentifier: "backToProfileFromFeedback", sender: self)
    }
    

}
