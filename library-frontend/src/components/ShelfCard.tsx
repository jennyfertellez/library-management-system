import React from 'react';
import type { Shelf } from '../types/shelf';
import { Library, Edit, Trash2 } from 'lucide-react';
import { Link } from 'react-router-dom';

interface ShelfCardProps {
  shelf: Shelf;
  onEdit: (shelf: Shelf) => void;
  onDelete: (shelf: Shelf) => void;
}

const ShelfCard: React.FC<ShelfCardProps> = ({ shelf, onEdit, onDelete }) => {
  return (
    <div className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-blue-100 rounded-lg">
            <Library className="h-6 w-6 text-blue-600" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">{shelf.name}</h3>
            <p className="text-sm text-gray-600">{shelf.bookCount} books</p>
          </div>
        </div>

        <div className="flex gap-2">
          <button
            onClick={(e) => {
              e.preventDefault();
              onEdit(shelf);
            }}
            className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg"
            title="Edit shelf"
          >
            <Edit className="h-4 w-4" />
          </button>
          <button
            onClick={(e) => {
              e.preventDefault();
              onDelete(shelf);
            }}
            className="p-2 text-red-600 hover:bg-red-50 rounded-lg"
            title="Delete shelf"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>

      {shelf.description && (
        <p className="text-gray-700 text-sm mb-4">{shelf.description}</p>
      )}

      {/* Book Preview */}
      {shelf.books && shelf.books.length > 0 && (
        <div className="border-t pt-4">
          <p className="text-sm text-gray-600 mb-2">Recent books:</p>
          <div className="flex gap-2 flex-wrap">
            {shelf.books.slice(0, 3).map((book) => (
              <Link
                key={book.id}
                to={`/books/${book.id}`}
                className="text-sm text-blue-600 hover:text-blue-700 hover:underline"
              >
                {book.title}
              </Link>
            ))}
            {shelf.books.length > 3 && (
              <span className="text-sm text-gray-500">
                +{shelf.books.length - 3} more
              </span>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ShelfCard;