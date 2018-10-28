//
//  ViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/4/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseAuth
import FirebaseCore
import Firebase
import FirebaseDatabase

class ViewController: UIViewController, UITextFieldDelegate {
    
    @IBOutlet var signInButton: UIButton!
    @IBOutlet var emailTextField: UITextField!
    @IBOutlet var passwordTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        if signInButton != nil {
            signInButton.layer.cornerRadius = 5
            signInButton.clipsToBounds = true
//            checkIfSignedIn()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func signInFirebase(_ sender: Any) {
        checkIfAdmin()
        
        let userEmail = emailTextField.text
        let userPassword = passwordTextField.text
        var authentication: Auth = Auth.auth()
        
        if userEmail != nil && userPassword != nil {
            authentication.signIn(withEmail: userEmail!, password: userPassword!) { (user, error) in
                if let firerror = error {
                    self.showAlert(headingAlert: "Could not sign into CloudFolio.", messageAlert: (error?.localizedDescription)!, actionTitle: "Retry", handleAction: { (action) in
                        
                    })
                    self.checkIfAdmin()
                } else {
                    self.moveToUserPage()
                    print ("MOVE")
                    self.checkIfAdmin()
                }
            }
        }
    }
    
    func checkIfSignedIn() {
        if Auth.auth().currentUser != nil {
            print("Current User is " + (Auth.auth().currentUser?.email)!)
            self.moveToUserPage()
        }
    }
    
    func moveToUserPage () {
        var storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let userPageStoryBoardViewController: UIViewController = storyBoard.instantiateViewController(withIdentifier: "UserPageController")
        self.present(userPageStoryBoardViewController, animated: true, completion: nil)
    }
    
    func checkIfAdmin () {
        if emailTextField.text == "admin" {
            if passwordTextField.text == "keycodepassword" {
                moveToAdminPage()
            }
        }
    }
    
    func moveToAdminPage () {
        var storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let userPageStoryBoardViewController: UIViewController = storyBoard.instantiateViewController(withIdentifier: "adminPage")
        self.present(userPageStoryBoardViewController, animated: true, completion: nil)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    func showAlert (headingAlert: String, messageAlert: String, actionTitle: String, handleAction: @escaping (_ action: UIAlertAction) -> ()) {
        var alert: UIAlertController = UIAlertController(title: headingAlert, message: messageAlert, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: actionTitle, style: UIAlertActionStyle.default, handler: handleAction))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
}
