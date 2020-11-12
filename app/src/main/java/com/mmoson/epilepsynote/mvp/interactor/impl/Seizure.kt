package com.mmoson9.epilepsynote.mvp.interactor.impl

data class Seizure(
    var id: String,
    var date: String,
    var size: String,
    var description: String?,
    var timestamp: String?
) {
    constructor() : this("", "", "", "", "")
}