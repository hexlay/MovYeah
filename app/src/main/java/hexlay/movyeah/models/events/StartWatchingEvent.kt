package hexlay.movyeah.models.events

import hexlay.movyeah.api.models.Movie

class StartWatchingEvent(val item: Movie, var identifier: String = "")