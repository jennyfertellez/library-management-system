import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { bookService } from '../services/bookService';
import type { Book } from '../types/book';
import EditBookModal from '../components/EditBookModal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';
import { ReadingStatus } from '../types/book';
import { ArrowLeft, Edit, Trash2, Star, Calendar, BookOpen } from 'lucide-react';

const statusColors = {
  [ReadingStatus.TO_READ]: 'bg-blue-100 text-blue-800',
  [ReadingStatus.CURRENTLY_READING]: 'bg-yellow-100 text-yellow-800',
  [ReadingStatus.FINISHED]: 'bg-green-100 text-green-800',
  [ReadingStatus.DNF]: 'bg-red-100 text-red-800',
};

const statusLabels = {
  [ReadingStatus.TO_READ]: 'To Read',
  [ReadingStatus.CURRENTLY_READING]: 'Currently Reading',
  [ReadingStatus.FINISHED]: 'Finished',
  [ReadingStatus.DNF]: 'Did Not Finish',
};

const BookDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  useEffect(() => {
    const fetchBook = async () => {
      if (!id) return;

      try {
        setLoading(true);
        const data = await bookService.getBookById(parseInt(id));
        setBook(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching book:', err);
        setError('Failed to load book details');
      } finally {
        setLoading(false);
      }
    };

    fetchBook();
  }, [id]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error || !book) {
    return (
      <div>
        <Link to="/books" className="flex items-center text-blue-600 hover:text-blue-700 mb-4">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Books
        </Link>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-800">{error || 'Book not found'}</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Back Button */}
      <Link to="/books" className="flex items-center text-blue-600 hover:text-blue-700 mb-6">
        <ArrowLeft className="h-4 w-4 mr-2" />
        Back to Books
      </Link>

      {/* Book Detail Card */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        <div className="md:flex">
          {/* Book Cover */}
          <div className="md:w-1/3 lg:w-1/4 bg-gray-100 flex items-center justify-center p-8">
            {book.thumbnailUrl ? (
              <img
                src={book.thumbnailUrl}
                alt={book.title}
                className="max-w-full h-auto rounded-lg shadow-md"
              />
            ) : (
              <div className="w-48 h-64 bg-gray-200 rounded-lg flex items-center justify-center">
                <BookOpen className="h-16 w-16 text-gray-400" />
              </div>
            )}
          </div>

          {/* Book Info */}
          <div className="md:w-2/3 lg:w-3/4 p-8">
            {/* Title and Actions */}
            <div className="flex justify-between items-start mb-4">
              <div>
                <h1 className="text-3xl font-bold text-gray-900 mb-2">{book.title}</h1>
                {book.author && (
                  <p className="text-xl text-gray-600">by {book.author}</p>
                )}
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => setIsEditModalOpen(true)}
                  className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg"
                  title="Edit book"
                >
                  <Edit className="h-5 w-5" />
                </button>
                <button
                  onClick={() => setIsDeleteModalOpen(true)}
                  className="p-2 text-red-600 hover:bg-red-50 rounded-lg"
                  title="Delete book"
                >
                  <Trash2 className="h-5 w-5" />
                </button>
                <DeleteConfirmModal
                   isOpen={isDeleteModalOpen}
                   title="Delete Book"
                   message={`Are you sure you want to delete "${book.title}"? This action cannot be undone.`}
                   onConfirm={async () => {
                     await bookService.deleteBook(book.id);
                     navigate('/books');
                   }}
                   onCancel={() => setIsDeleteModalOpen(false)}
                 />
              </div>
            </div>

            {/* Status Badge */}
            <div className="mb-6">
              <span className={`inline-block px-4 py-2 rounded-full text-sm font-medium ${statusColors[book.status]}`}>
                {statusLabels[book.status]}
              </span>
            </div>

            {/* Meta Information */}
            <div className="grid grid-cols-2 gap-4 mb-6">
              {book.isbn && (
                <div>
                  <p className="text-sm text-gray-600">ISBN</p>
                  <p className="font-medium text-gray-900">{book.isbn}</p>
                </div>
              )}
              {book.publishedDate && (
                <div>
                  <p className="text-sm text-gray-600">Published</p>
                  <p className="font-medium text-gray-900">{book.publishedDate}</p>
                </div>
              )}
              {book.pageCount && (
                <div>
                  <p className="text-sm text-gray-600">Pages</p>
                  <p className="font-medium text-gray-900">{book.pageCount}</p>
                </div>
              )}
              {book.rating && (
                <div>
                  <p className="text-sm text-gray-600">Rating</p>
                  <div className="flex items-center">
                    <Star className="h-5 w-5 text-yellow-500 fill-yellow-500 mr-1" />
                    <span className="font-medium text-gray-900">{book.rating}/5</span>
                  </div>
                </div>
              )}
            </div>

            {/* Finished Date */}
            {book.finishedDate && (
              <div className="mb-6">
                <div className="flex items-center text-gray-600">
                  <Calendar className="h-4 w-4 mr-2" />
                  <span className="text-sm">
                    Finished on {new Date(book.finishedDate).toLocaleDateString()}
                  </span>
                </div>
              </div>
            )}

            {/* Description */}
            {book.description && (
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">Description</h2>
                <p className="text-gray-700 leading-relaxed">{book.description}</p>
              </div>
            )}

            {/* Notes */}
            {book.notes && (
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">My Notes</h2>
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                  <p className="text-gray-700 whitespace-pre-wrap">{book.notes}</p>
                </div>
              </div>
            )}

            {/* Timestamps */}
            <div className="text-sm text-gray-500 pt-4 border-t">
              <p>Added: {new Date(book.createdAt).toLocaleDateString()}</p>
              {book.updatedAt !== book.createdAt && (
                <p>Updated: {new Date(book.updatedAt).toLocaleDateString()}</p>
              )}
            </div>
          </div>
        </div>
      </div>
      <EditBookModal
         book={book}
         isOpen={isEditModalOpen}
         onClose={() => setIsEditModalOpen(false)}
         onBookUpdated={() => {
           // Refresh book data
           if (id) {
             bookService.getBookById(parseInt(id)).then(setBook);
           }
         }}
       />
    </div>
  );
};

export default BookDetailPage;