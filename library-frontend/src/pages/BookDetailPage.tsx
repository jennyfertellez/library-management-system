import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  ArrowLeft,
  Edit,
  Trash2,
  Star,
  Calendar,
  BookOpen,
} from 'lucide-react';

import { bookService } from '../services/bookService';
import { shelfService } from '../services/shelfService';

import type { Book } from '../types/book';
import type { Shelf } from '../types/shelf';
import { ReadingStatus } from '../types/book';

import EditBookModal from '../components/EditBookModal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';

const statusColors = {
  [ReadingStatus.TO_READ]: 'bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-300',
  [ReadingStatus.CURRENTLY_READING]: 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-800 dark:text-yellow-300',
  [ReadingStatus.FINISHED]: 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300',
  [ReadingStatus.DNF]: 'bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-300',
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
  const [shelves, setShelves] = useState<Shelf[]>([]);
  const [showShelfDropdown, setShowShelfDropdown] = useState(false);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  /* Fetch book */
  useEffect(() => {
    if (!id) return;

    const fetchBook = async () => {
      try {
        setLoading(true);
        const data = await bookService.getBookById(Number(id));
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

  /* Fetch shelves */
  useEffect(() => {
    const fetchShelves = async () => {
      try {
        const data = await shelfService.getAllShelves();
        setShelves(data);
      } catch (err) {
        console.error('Error fetching shelves:', err);
      }
    };

    fetchShelves();
  }, []);

  const addToShelf = async (shelfId: number) => {
    if (!book) return;

    try {
      await shelfService.addBookToShelf(shelfId, book.id);
      setShowShelfDropdown(false);
      alert('Book added to shelf');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to add book to shelf');
    }
  };

  /* Loading */
  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600 dark:border-blue-400" />
      </div>
    );
  }

  /* Error */
  if (error || !book) {
    return (
      <div>
        <Link
          to="/books"
          className="mb-4 flex items-center text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-500"
        >
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Books
        </Link>

        <div className="rounded-lg border border-red-200 dark:border-red-800 bg-red-50 dark:bg-red-900/20 p-4">
          <p className="text-red-800 dark:text-red-200">{error || 'Book not found'}</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Back Button */}
      <Link
        to="/books"
        className="mb-6 flex items-center text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-500"
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Back to Books
      </Link>

      {/* Book Card */}
      <div className="overflow-hidden rounded-lg bg-white dark:bg-gray-800 shadow-lg dark:shadow-gray-900/50 border border-transparent dark:border-gray-700">
        <div className="md:flex">
          {/* Cover */}
          <div className="flex items-center justify-center bg-gray-100 dark:bg-gray-700 p-8 md:w-1/3 lg:w-1/4">
            {book.thumbnailUrl ? (
              <img
                src={book.thumbnailUrl}
                alt={book.title}
                className="h-auto max-w-full rounded-lg shadow-md"
              />
            ) : (
              <div className="flex h-64 w-48 items-center justify-center rounded-lg bg-gray-200 dark:bg-gray-600">
                <BookOpen className="h-16 w-16 text-gray-400 dark:text-gray-500" />
              </div>
            )}
          </div>

          {/* Info */}
          <div className="p-8 md:w-2/3 lg:w-3/4">
            {/* Title + Actions */}
            <div className="mb-4 flex items-start justify-between">
              <div>
                <h1 className="mb-2 text-3xl font-bold text-gray-900 dark:text-gray-100">
                  {book.title}
                </h1>
                {book.author && (
                  <p className="text-xl text-gray-600 dark:text-gray-400">by {book.author}</p>
                )}
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => setIsEditModalOpen(true)}
                  className="rounded-lg p-2 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/30"
                >
                  <Edit className="h-5 w-5" />
                </button>

                <button
                  onClick={() => setIsDeleteModalOpen(true)}
                  className="rounded-lg p-2 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30"
                >
                  <Trash2 className="h-5 w-5" />
                </button>

                {/* Add to shelf */}
                <div className="relative">
                  <button
                    onClick={() => setShowShelfDropdown((v) => !v)}
                    className="rounded-lg bg-gray-100 dark:bg-gray-700 px-4 py-2 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600"
                  >
                    Add to Shelf
                  </button>

                  {showShelfDropdown && (
                    <div className="absolute right-0 z-10 mt-2 w-56 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-lg dark:shadow-gray-900/50">
                      {shelves.length === 0 ? (
                        <p className="p-3 text-sm text-gray-500 dark:text-gray-400">
                          No shelves yet
                        </p>
                      ) : (
                        shelves.map((shelf) => (
                          <button
                            key={shelf.id}
                            onClick={() => addToShelf(shelf.id)}
                            className="w-full rounded px-3 py-2 text-left text-sm text-gray-900 dark:text-gray-100 hover:bg-gray-100 dark:hover:bg-gray-700"
                          >
                            {shelf.name}
                          </button>
                        ))
                      )}
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Status */}
            <div className="mb-6">
              <span
                className={`inline-block rounded-full px-4 py-2 text-sm font-medium ${statusColors[book.status]}`}
              >
                {statusLabels[book.status]}
              </span>
            </div>

            {/* Metadata */}
            <div className="mb-6 grid grid-cols-2 gap-4">
              {book.isbn && (
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">ISBN</p>
                  <p className="font-medium text-gray-900 dark:text-gray-100">{book.isbn}</p>
                </div>
              )}

              {book.publishedDate && (
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">Published</p>
                  <p className="font-medium text-gray-900 dark:text-gray-100">{book.publishedDate}</p>
                </div>
              )}

              {book.pageCount && (
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">Pages</p>
                  <p className="font-medium text-gray-900 dark:text-gray-100">{book.pageCount}</p>
                </div>
              )}

              {book.rating && (
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">Rating</p>
                  <div className="flex items-center">
                    <Star className="mr-1 h-5 w-5 fill-yellow-500 dark:fill-yellow-400 text-yellow-500 dark:text-yellow-400" />
                    <span className="font-medium text-gray-900 dark:text-gray-100">{book.rating}/5</span>
                  </div>
                </div>
              )}
            </div>

            {book.finishedDate && (
              <div className="mb-6 flex items-center text-gray-600 dark:text-gray-400">
                <Calendar className="mr-2 h-4 w-4" />
                Finished on{' '}
                {new Date(book.finishedDate).toLocaleDateString()}
              </div>
            )}

            {book.description && (
              <div className="mb-6">
                <h2 className="mb-2 text-lg font-semibold text-gray-900 dark:text-gray-100">Description</h2>
                <p className="leading-relaxed text-gray-700 dark:text-gray-300">
                  {book.description}
                </p>
              </div>
            )}

            {book.notes && (
              <div className="mb-6">
                <h2 className="mb-2 text-lg font-semibold text-gray-900 dark:text-gray-100">My Notes</h2>
                <div className="rounded-lg border border-yellow-200 dark:border-yellow-800 bg-yellow-50 dark:bg-yellow-900/20 p-4">
                  <p className="whitespace-pre-wrap text-gray-700 dark:text-gray-300">
                    {book.notes}
                  </p>
                </div>
              </div>
            )}

            <div className="border-t border-gray-200 dark:border-gray-700 pt-4 text-sm text-gray-500 dark:text-gray-400">
              <p>Added: {new Date(book.createdAt).toLocaleDateString()}</p>
              {book.updatedAt !== book.createdAt && (
                <p>
                  Updated:{' '}
                  {new Date(book.updatedAt).toLocaleDateString()}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      <EditBookModal
        book={book}
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onBookUpdated={async () => {
          if (id) {
            const updated = await bookService.getBookById(Number(id));
            setBook(updated);
          }
        }}
      />

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
  );
};

export default BookDetailPage;