import React from 'react';
import type { Book } from '../types/book';
import { ReadingStatus } from '../types/book';
import { BookOpen, Star } from 'lucide-react';
import { Link } from 'react-router-dom';

interface BookCardProps {
  book: Book;
}

const statusColors = {
  [ReadingStatus.TO_READ]: 'bg-blue-100 text-blue-800',
  [ReadingStatus.CURRENTLY_READING]: 'bg-yellow-100 text-yellow-800',
  [ReadingStatus.FINISHED]: 'bg-green-100 text-green-800',
  [ReadingStatus.DNF]: 'bg-red-100 text-red-800',
};

const statusLabels = {
  [ReadingStatus.TO_READ]: 'To Read',
  [ReadingStatus.CURRENTLY_READING]: 'Reading',
  [ReadingStatus.FINISHED]: 'Finished',
  [ReadingStatus.DNF]: 'DNF',
};

const BookCard: React.FC<BookCardProps> = ({ book }) => {
  return (
    <Link to={`/books/${book.id}`}>
      <div className="bg-white rounded-lg shadow hover:shadow-xl transition-all duration-300 p-4 cursor-pointer transform hover:-translate-y-1">
        {/* Book Cover or Placeholder */}
        <div className="aspect-[2/3] bg-gray-100 rounded-md mb-3 flex items-center justify-center overflow-hidden">
          {book.thumbnailUrl ? (
            <img
              src={book.thumbnailUrl}
              alt={book.title}
              className="w-full h-full object-cover"
            />
          ) : (
            <BookOpen className="h-12 w-12 text-gray-400" />
          )}
        </div>

        {/* Book Info */}
        <h3 className="font-semibold text-gray-900 line-clamp-2 mb-1">
          {book.title}
        </h3>

        <p className="text-sm text-gray-600 mb-2">
          {book.author || 'Unknown Author'}
        </p>

        {/* Status Badge */}
        <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${statusColors[book.status]}`}>
          {statusLabels[book.status]}
        </span>

        {/* Rating */}
        {book.rating && (
          <div className="flex items-center mt-2">
            <Star className="h-4 w-4 text-yellow-500 fill-yellow-500" />
            <span className="ml-1 text-sm text-gray-700">{book.rating}/5</span>
          </div>
        )}
      </div>
    </Link>
  );
};

export default BookCard;