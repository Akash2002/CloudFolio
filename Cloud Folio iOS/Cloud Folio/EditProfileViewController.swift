//
//  EditProfileViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/6/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseDatabase

class EditProfileViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource{

    @IBOutlet var schoolNameText: UITextField!
    @IBOutlet var fullNameText: UITextField!
    @IBOutlet var pickerView: UIPickerView!
    
    var grades = [1,2,3,4,5,6,7,8,9,10,11,12]
    var grade: Int = 1
    var appuser: AppUser = AppUser()
    var dbRef: DatabaseReference = Database.database().reference().child("Users")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        dbRef = dbRef.child(appuser.getCurrentUserID())
        dbRef.observeSingleEvent(of: .value) { (snapshot) in
            if let val = snapshot.value as? [String: Any?] {
                self.schoolNameText.text = val["School"] as! String
                self.fullNameText.text = val["FullName"] as! String
               
            }
        }
    }

    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1 //rows
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return String(grades[row])
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return grades.count
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        grade = grades[row]
    }
    
    @IBAction func saveProfileData(_ sender: Any) {
        print ("Hello")
        if let schoolname = schoolNameText.text {
            if schoolname.count > 3 {
                dbRef.child("School").setValue(schoolname)
            } else {
                showAlert(headingAlert: "Invalid School Name.", messageAlert: "School name is too short.", actionTitle: "Retry") { (action) in
                    
                }
            }
        }
        if let fullname = fullNameText.text {
            if fullname.count > 0 {
                dbRef.child("FullName").setValue(fullname)
            } else {
                showAlert(headingAlert: "Name field left empty.", messageAlert: "Please enter a valid name.", actionTitle: "Ok") { (action) in
                    
                }
            }
        }
        
        dbRef.child("GradeLevel").setValue(String(grade))
        self.navigationController?.popViewController(animated: true)
    }
    
    func showAlert (headingAlert: String, messageAlert: String, actionTitle: String, handleAction: @escaping (_ action: UIAlertAction) -> ()) {
        var alert: UIAlertController = UIAlertController(title: headingAlert, message: messageAlert, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: actionTitle, style: UIAlertActionStyle.default, handler: handleAction))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
}
