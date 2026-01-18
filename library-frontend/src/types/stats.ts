export interface ReadingStats {
    totalBooks: number;
    booksRead: number;
    booksToRead: number;
    currentlyReading: number;
    booksDidNotFinish: number;
    currentYear: number;
    booksReadThisYear: number;
    booksAddedThisYear: number;
    averageRating: number;
    ratedBooks: number;
    ratingDistribution: { [key: number]: number };
    uniqueAuthors: number;
    topAuthors: { [key: string]: number };
    booksPerMonth: number;
    averagePagesPerBook: number;
    booksReadByYear: { [key: number]: number };
    }