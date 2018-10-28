//
//  BooksTableViewController.swift
//  
//
//  Created by Akash  Veerappan on 4/15/18.
//

import UIKit
import Firebase

class BooksTableCell: UITableViewCell {
    
    @IBOutlet var bookNameText: UILabel!
    @IBOutlet var bookGenreText: UILabel!
    @IBOutlet var bookAuthorText: UILabel!
    @IBOutlet var bookLexileText: UILabel!
    @IBOutlet var bookStockText: UILabel!
    
}

class BooksTableViewController: UITableViewController {
    
    @IBOutlet var booksTableView: UITableView!
    var dbref: DatabaseReference = Database.database().reference().child("Books")
    
    var bookNames: [String] = ["Anthem", "Odyssey", "Macbeth"]
    var genreNames: [String] = ["Dystopia", "Epic", "Tragedy"]
    var bookRefIterator: [String] = []
    var booksArray: [Book] = []
    var bookNameForTransfer: String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.booksArray.removeAll()
        self.bookRefIterator.removeAll()
        fetchBookData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return booksArray.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as? BooksTableCell
        if (!(indexPath.row > booksArray.count)) {
            cell?.bookNameText.text = booksArray[indexPath.row].bookName
            cell?.bookGenreText.text = booksArray[indexPath.row].bookGenre
            cell?.bookLexileText.text = booksArray[indexPath.row].lexileLevel
            cell?.bookStockText.text = booksArray[indexPath.row].bookStock
            cell?.bookAuthorText.text = booksArray[indexPath.row].authorName
        }
        
        return cell!
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print (indexPath.row)
        if indexPath.row <= booksArray.count {
            if indexPath.row < booksArray.count {
                bookNameForTransfer = booksArray[indexPath.row].bookName
                moveToDetailsPage()
            }
        }
    }
    
    func fetchBookData () {
        dbref.observeSingleEvent(of: .value) { (snapshot) in
            for books in snapshot.children {
                self.bookRefIterator.append((books as! DataSnapshot).key)
            }
            
            for name in self.bookRefIterator {
                self.dbref.child(name).observeSingleEvent(of: .value, with: { (snapshot) in
                    var tempBook: Book = Book()
                    if let value = snapshot.value as? [String: Any?] {
                        tempBook.authorName = value["Author"]! as! String
                        tempBook.bookDescription = value["Description"]! as! String
                        tempBook.bookGenre = value["Genre"]! as! String
                        tempBook.lexileLevel = value["Lexile"]! as! String
                        tempBook.bookName = value["Name"]! as! String
                        tempBook.numPages = String(value["Pages"]! as! Int)
                        tempBook.bookStock = String(value["Stock"]! as! Int)
                        tempBook.bookType = value["Type"]! as! String
                        self.booksArray.append(tempBook)
                        
                        DispatchQueue.main.async(execute: {
                            self.booksTableView.reloadData()
                        })
                        
                    }
                })
            }
        }
    }
    
    func moveToDetailsPage () {
        BookDetailsViewController.bookName = bookNameForTransfer
        self.performSegue(withIdentifier: "BookDetailsViewController", sender: self)
    }
    
}
