package com.appnotification.notificationhistorylog.Model

class Item {
    var text: String? = null
    var subText: String? = null
    var isExpandable = false

    constructor() {}
    constructor(text: String?, subText: String?, expandable: Boolean) {
        this.text = text
        this.subText = subText
        isExpandable = expandable
    }
}