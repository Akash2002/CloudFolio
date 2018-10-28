//
//  CreateAccountViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/5/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseCore
import FirebaseAuth
import FirebaseDatabase

class CreateAccountViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet var fullNameTextField: UITextField!
    @IBOutlet var passwordFirstEntryField: UITextField!
    @IBOutlet var passwordReEntryField: UITextField!
    @IBOutlet var emailTextField: UITextField!
    @IBOutlet var schoolNameField: UITextField!
    @IBOutlet var gradeLevelField: UILabel!
    @IBOutlet var teacherOrStudentSegment: UISegmentedControl!
    @IBOutlet var gradeSlider: UISlider!
    @IBOutlet var userEnteredStatusLabel: UILabel!
    
    var gradeLevel: Int = 8
    var userType: String = "Student"
    var fullUserName: String = ""
    var userPasswordOne: String = ""
    var userPasswordTwo: String = ""
    var userEmail: String = ""
    var schoolName: String = ""
    var auth: Auth = Auth.auth()
    var p1 = false
    var p2 = false
    var n1 = false
    var e1 = false
    var s1 = false
    
    var ref: DatabaseReference! = Database.database().reference().child("Users")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func createAccount(_ sender: Any) {
        gradeLevel = Int(gradeSlider.value)
        userType = findSegmentValue()
        if let a = fullNameTextField.text {
            fullUserName = a
        }
        if let b = passwordFirstEntryField.text {
            userPasswordOne = b
        }
        if let c = passwordReEntryField.text {
            userPasswordTwo = c
        }
        if let d = emailTextField.text {
            userEmail = d
        }
        if let e = schoolNameField.text {
            schoolName = e
        }
        
        auth.createUser(withEmail: userEmail, password: userPasswordOne) { (user, error) in
            if let firerror = error {
                self.showAlert(headingAlert: "Could not create account.", messageAlert: (firerror.localizedDescription), actionTitle: "Retry", handleAction: { (action) in
                })
            } else {
                self.auth.signIn(withEmail: self.userEmail, password: self.userPasswordOne, completion: { (user, error) in
                    if let f_error = error {
                        print (error?.localizedDescription)
                    } else {
                        self.enterUserData()
                        var storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
                        let userPageStoryBoardViewController: UIViewController = storyBoard.instantiateViewController(withIdentifier: "UserPageController")
                        self.present(userPageStoryBoardViewController, animated: true, completion: nil)
                    }
                })
            }
        }
       
    }
    
    @IBAction func onSliderValueChanged(_ sender: Any) {
        gradeLevelField.text = String(Int(gradeSlider.value))
    }
    
    func findSegmentValue () -> String {
        switch teacherOrStudentSegment.selectedSegmentIndex {
        case 0:
            return "Student"
        case 1:
            return "Teacher"
        default:
            return "Student"
        }
    }
    
    func modifyString (string: String) -> String {
        var modString: String = ""
        if let index = string.index(of: "@") {
            modString = String(string[..<index])
        }
        return modString
    }
    
    func enterUserData () {
        self.ref.child(modifyString(string: userEmail)).setValue(["EmailID": userEmail])
        ref.child(modifyString(string: userEmail)).child("FullName").setValue(fullUserName)
        ref.child(modifyString(string: userEmail)).child("GradeLevel").setValue(String(gradeLevel))
        ref.child(modifyString(string: userEmail)).child("School").setValue(schoolName)
        ref.child(modifyString(string: userEmail)).child("UserType").setValue(userType)
    }
    
    @IBAction func onSegmentValueChanged(_ sender: Any) {
        if teacherOrStudentSegment.selectedSegmentIndex == 0 {
            self.gradeSlider.isEnabled = true
            self.gradeLevelField.text = "8"
            print ("Student")
        } else {
            self.gradeSlider.isEnabled = false
            self.gradeLevelField.text = "..."
            self.gradeLevelField.isEnabled = false
        }
    }
    
    @IBAction func onEmailTextFieldEditingEnded(_ sender: Any) {
        if let email = emailTextField.text as? String {
            if email.range (of: ".com") != nil {
                if email.range (of: "@") != nil {
                    if email.count >= 7 {
                        userEnteredStatusLabel.textColor = UIColor.blue
                        userEnteredStatusLabel.text = "Email is correct."
                        e1 = true
                    } else if email.count < 7 && email.count > 0{
                        userEnteredStatusLabel.textColor = UIColor.red
                        userEnteredStatusLabel.text = "Email is too short."
                        e1 = false
                    } else {
                        userEnteredStatusLabel.textColor = UIColor.red
                        userEnteredStatusLabel.text = "Please enter an email address."
                        e1 = false
                    }
                } else {
                    userEnteredStatusLabel.textColor = UIColor.red
                    userEnteredStatusLabel.text = "Email entered is incorrect."
                    e1 = false
                }
            } else {
                userEnteredStatusLabel.textColor = UIColor.red
                userEnteredStatusLabel.text = "Email entered is incorrect"
                e1 = false
            }
        }
    }
    
    @IBAction func onFirstPwdFinishedEditing(_ sender: Any) {
        if let pwd = passwordFirstEntryField.text {
            if pwd.count >= 7 {
                userEnteredStatusLabel.textColor = UIColor.blue
                userEnteredStatusLabel.text = "Password is correct."
                p1 = true
            } else if pwd.count < 7 && pwd.count > 0 {
                userEnteredStatusLabel.textColor = UIColor.red
                userEnteredStatusLabel.text = "Password is too short."
                p1 = false
            } else {
                userEnteredStatusLabel.textColor = UIColor.red
                userEnteredStatusLabel.text = "Please enter a password."
                p1 = false
            }
        }
    }
    
    @IBAction func onSecondPwdFinishedEditing(_ sender: Any) {
        if let pwd = passwordReEntryField.text {
            if pwd == passwordFirstEntryField.text! {
                if (pwd.count > 0) {
                    userEnteredStatusLabel.textColor = UIColor.blue
                    userEnteredStatusLabel.text = "Password has been confirmed."
                    p2 = true
                } else {
                    userEnteredStatusLabel.textColor = UIColor.red
                    userEnteredStatusLabel.text = "Please enter a password."
                    p2 = false
                }
            } else {
                userEnteredStatusLabel.textColor = UIColor.red
                userEnteredStatusLabel.text = "Password entered does not match."
                p2 = false
            }
        }
    }
    
    @IBAction func onSchoolEditingEnded(_ sender: Any) {
        if let pwd = schoolNameField.text {
            if (!(pwd.count > 0)) {
                userEnteredStatusLabel.textColor = UIColor.red
                userEnteredStatusLabel.text = "Please enter a school name."
                s1 = false
            } else {
                s1 = true
            }
        }
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
