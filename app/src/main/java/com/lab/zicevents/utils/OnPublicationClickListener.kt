package com.lab.zicevents.utils

import com.lab.zicevents.data.model.database.publication.Publication
import com.lab.zicevents.data.model.database.user.User

interface OnPublicationClickListener {
    fun onPublicationClick(publication: Publication, publicationOwner: User)
    fun onImageProfileClick(userId: String)
}