# Multi-Source Search Testing Log

## Date: Feb 11, 2026

### Test Cases

#### ISBN Search
- [✅] Valid book ISBN (9780593641194 - First-time Caller)
- Expected: Results from OpenLibrary and/or Google Books
- Actual:
- Status: ✅ / ❌

- [✅] Manga ISBN (9781569319000 - Naruto Vol 1)
- Expected: Results from multiple sources including Jikan
- Actual:
- Status: ✅ / ❌

- [❌] Invalid ISBN (123)
- Expected: Error message or no results 
- Actual:
- Status: ✅ / ❌

#### Title Search 
- [❌] Common book title ("Harry Potter")
- Expected: Multiple results from different sources
- Actual:
- Status: ✅ / ❌

- [❌] Manga title ("Naruto")
- Expected: "No results found" message 
- Actual:
- Status: ✅ / ❌

- [✅] Obscure title ("asdfghjkl")
- Expected: "No results found" message
- Actual: 
- Status: ✅ / ❌

#### User Flow
- [✅] Search → See multiple results → Click one → Book added
- [❌] Search → See one result → Automatically added
- [✅] Search → No results → Error message shown
- [✅] Search → Click "New Search" → Form resets
- [❌] Add manga with MAL ID -> Saves successfully

#### Edge Cases
- [✅] Empty search
- [❌] Very long title
- [❌] Special while loading 
- [✅] Network error handling

### Bugs Found
1. Duplicate ISBN is not allowed to load, but if we want to search to see the book options, no results are given
2. Book Dashboard - once the book is added, the only things that should be allowed to edit are the status, rating,
description and my notes.
3. Harry Potter is a common title, but only Jikan is pulling in a manga. How should the code handle common titles
and book series?
4. Volumes are not working when searching via Title 

### UI/UX Issues 
1. Fix the pop up when a book is added to a shelf.
2. Add an option to edit when a book was started and completed. 
3. Add a button to be able to add the books from your current dashboard to a shelf.
4. Delete books on shelf without deleting the shelf
5. Update/edit tab can also add/delete books option
