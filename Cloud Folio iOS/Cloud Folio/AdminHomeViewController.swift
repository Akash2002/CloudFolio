//
//  AdminHomeViewController.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 5/8/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit

class AdminHomeViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBOutlet var signOut: UIBarButtonItem!
    
    @IBAction func onSignOutClick(_ sender: Any) {
        var storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let userPageStoryBoardViewController: UIViewController = storyBoard.instantiateViewController(withIdentifier: "loginViewController")
        self.present(userPageStoryBoardViewController, animated: true, completion: nil)
    }
}
