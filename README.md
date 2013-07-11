# DuckDuckGo for Android

Please review the LICENSE and CONTRIBUTING guidelines.

## Mission statement

<Insert plan for world domination>

### How to give feedback

Contact us at android@duckduckgo.com if you have questions or want to chat.

<Possibly more information on community platform, mailing lists, ...>


## Getting started

Info to make development easier, which tools we use for 
- developing
- building
- testing

We're currently still setting this up.

## Things you can help with ( aka low hanging fruit )
- bug fixes
- layout fixes
- security fixes
- improving the test suite

Also, check out the issue tracker.


## Pre-release tests

// maybe we should move this into a separate file

### Generic
- Home screen switching (saved, recent, stories)
- Use external browsing check (check for looping!)
- Autocomplete

### Recents
- Check toggle
- Check clearing
- Verify long press
- Verify ( + ) functionality for searches

### Stories
- Images
- Long press
- Make sure new sources come in and ones marked default are turned on.

### Readability
- Toggle only shows for story

### Share
- Long press story and try
- Hit back
- Tap story
- Tap action menu
- Tap share

### Saving (only for stories)
- Go into story
- Hit action button
- Hit save
- Hit back
- Check saved view
- Unsave from saved view
- Re-save from recents
- Unsave from home view
- Verify

### Changing source
- Make sure you can and it works appropriately

### Search
- Navigation
- Sharing
- Search Box (loading graphic, appropriate behavior on URL vs. query, clearing)
