//
//  AdminAddBookViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/29/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit
import FirebaseAuth
import FirebaseCore
import FirebaseDatabase

class AdminAddBookViewController: ViewController {
    @IBOutlet var bookNameField: UITextField!
    @IBOutlet var authorField: UITextField!
    @IBOutlet var genreField: UITextField!
    @IBOutlet var descriptionField: UITextField!
    @IBOutlet var lexileField: UITextField!
    @IBOutlet var numPagesField: UITextField!
    @IBOutlet var stockSlider: UISlider!
    @IBOutlet var stockLabel: UILabel!
    @IBOutlet var bookTypeSegment: UISegmentedControl!
    
    var stock: Int = 25
    var type = "Fiction"
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onStockValChanged(_ sender: Any) {
        stock = Int(stockSlider.value)
        stockLabel.text = String(stock)
    }
    
    @IBAction func onBookTypeChanged(_ sender: Any) {
        switch bookTypeSegment.selectedSegmentIndex {
        case 0:
            type = "Fiction"
        case 1:
            type = "Non-Fiction"
        default:
            type = "Fiction"
        }
    }
    
    @IBAction func onAddBookClick(_ sender: Any) {
        var dbRef: DatabaseReference = Database.database().reference().child("Books")
        if var bookName = bookNameField.text {
            if (bookName.count > 0) {
            dbRef = dbRef.child(bookName)
            if var author = authorField.text {
                if (author.count > 0) {
                if var genre = genreField.text {
                    if genre.count > 0 {
                    if var description = descriptionField.text {
                        if description.count > 25 {
                        if var lexile = lexileField.text {
                            if Int(lexile) != nil {
                                if Int(lexile)! < 2000 && Int(lexile)! > 0 {
                                    if var numPages = numPagesField.text {
                                        if Int(numPages) != nil {
                                    dbRef.child("Author").setValue(author)
                                    dbRef.child("Genre").setValue(genre)
                                    dbRef.child("Description").setValue(description)
                                    dbRef.child("Lexile").setValue(String(lexile) + "L")
                                    dbRef.child("Pages").setValue(Int(numPages))
                                    dbRef.child("Type").setValue(type)
                                    dbRef.child("Stock").setValue(stock)
                                    dbRef.child("Name").setValue(bookName)
                                    self.navigationController?.popViewController(animated: true)
                                        } else {
                                            self.showAlert(headingAlert: "Pages is not a number.", messageAlert: "Please enter a proper number for the pages input field.", actionTitle: "Ok") { (action) in
                                        
                                            }
                                        }
                                    }
                                    } else {
                                    self.showAlert(headingAlert: "Lexile is not a valid number.", messageAlert: "According to the lexile standards, the maximum value is 2000 and the lowest is 0. Please enter a valid number between that range.", actionTitle: "Ok") { (action) in
                                        
                                    }
                                }
                                } else {
                                self.showAlert(headingAlert: "Lexile is not a number", messageAlert: "Please enter a proper number for the lexile input field", actionTitle: "Ok") { (action) in
                                    
                                }
                            }
                        }
                        } else {
                            self.showAlert(headingAlert: "Description field too short or left empty.", messageAlert: "Please enter the correct book's description that you want to add. It must be more than 25 characters.", actionTitle: "Ok") { (action) in
                                
                            }
                        }
                    }
                    } else {
                        self.showAlert(headingAlert: "Genre field left empty.", messageAlert: "Please enter the correct book's genre that you want to add.", actionTitle: "Ok") { (action) in
                            
                        }
                    }
                } else {
                    self.showAlert(headingAlert: "Author Name left empty.", messageAlert: "Please enter the correct book's name that you want to add.", actionTitle: "Ok") { (action) in
                        
                    }
                }
            }
            } else {
                self.showAlert(headingAlert: "Book Name left empty.", messageAlert: "Please enter the correct book's name that you want to add.", actionTitle: "Ok") { (action) in
                    
                }
            }
            }
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    override func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    override func showAlert (headingAlert: String, messageAlert: String, actionTitle: String, handleAction: @escaping (_ action: UIAlertAction) -> ()) {
        var alert: UIAlertController = UIAlertController(title: headingAlert, message: messageAlert, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: actionTitle, style: UIAlertActionStyle.default, handler: handleAction))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
}
