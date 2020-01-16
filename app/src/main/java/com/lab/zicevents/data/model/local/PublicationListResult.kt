package com.lab.zicevents.data.model.local

import com.lab.zicevents.data.model.database.publication.Publication

class PublicationListResult(
    val list: List<Publication>? = null,
    val error: Int? = null)