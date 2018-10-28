//
//  DateManager.swift
//  Cloud Folio
//
//  Created by Akash  Veerappan on 4/21/18.
//  Copyright © 2018 Akash Veerappan. All rights reserved.
//

import UIKit

class DateManager: NSObject {
    internal var date: Date = Date()
    internal var date2: Date = Date()
    internal var cal: Calendar = Calendar.current
    internal var year: Int
    internal var month: Int
    internal var day: Int
    internal var completeDateString1: String = String()
    internal var completeDateString2: String = String()
    
    override init() {
        year = cal.component(Calendar.Component.year, from: date)
        month = cal.component(Calendar.Component.month, from: date)
        day = cal.component(Calendar.Component.day, from: date)
        completeDateString1 = String(month) + "-" + String(day) + "-" + String(year);
    }
    
    func getDate () -> String {
        return completeDateString1
    }
    
    func addDaysToDate(_ numDaysToBeAdded: Int) -> String {
        date2 = cal.date(byAdding: Calendar.Component.day, value: numDaysToBeAdded, to: date)!
        var tyear = cal.component(Calendar.Component.year, from: date2)
        var tmonth = cal.component(Calendar.Component.month, from: date2)
        var tday = cal.component(Calendar.Component.day, from: date2)
        completeDateString2 = String(tmonth) + "-" + String(tday) + "-" + String(tyear);
        return completeDateString2
    }
    
    func getDuration (date1: Date, date2: Date) -> Int {
        let form = DateComponentsFormatter()
        form.maximumUnitCount = 2
        form.unitsStyle = .full
        form.allowedUnits = [.year, .month, .day]
        var s = form.string(from: date1, to: date2)!
        print ("Duration: " + s)
        var dIndex = s.index(of: " ")
        s = s.substring(to: dIndex!)
        return Int(s)!
    }
    
}
