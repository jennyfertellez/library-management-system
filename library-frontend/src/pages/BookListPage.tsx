import React, { useEffect, useState } from 'react';
import { bookService } from '../services/bookService';
import type { Book, PageResponse } from '../types/book';
import BookCard from '../components/BookCard';
import BookCardSkeleton from '../components/BookCardSkeleton';
import LoadingSpinner from '../components/LoadingSpinner';
import { Plus, BookOpen } from 'lucide-react';
import SearchBar from '../components/SearchBar';
import StatusFilter from '../components/StatusFilter';
import { ReadingStatus } from '../types/book';
import AddBookModal from '../components/AddBookModal';
import EmptyState from '../components/EmptyState';

const BookListPage: React.FC = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalBooks, setTotalBooks] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedStatus, setSelectedStatus] =
    useState<ReadingStatus | 'ALL'>('ALL');
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  const fetchBooks = async (
    pageNum: number,
    search?: string,
    status?: ReadingStatus | 'ALL'
  ) => {
    try {
      setLoading(true);
      let response: PageResponse<Book>;

      if (search) {
        response = await bookService.searchBooks(search, pageNum, 20);
      } else if (status && status !== 'ALL') {
        response = await bookService.getBooksByStatus(status, pageNum, 20);
      } else {
        response = await bookService.getAllBooks(pageNum, 20);
      }

      setBooks(response.content);
      setTotalPages(response.totalPages);
      setTotalBooks(response.totalElements);
      setError(null);
    } catch (err) {
      console.error('Error fetching books:', err);
      setError('Failed to load books.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks(page, searchTerm, selectedStatus);
  }, [page, searchTerm, selectedStatus]);

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4">
        <p className="text-red-800">{error}</p>
      </div>
    );
  }

  return (
    <div>
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">My Books</h1>
          <p className="text-gray-600 mt-1">
            {totalBooks} books in your library
          </p>
        </div>

        <button
          onClick={() => setIsAddModalOpen(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center"
        >
          <Plus className="h-5 w-5 mr-2" />
          Add Book
        </button>
      </div>

      {/* Search + Filters */}
      <div className="mb-6 space-y-4">
        <SearchBar
          onSearch={(term) => {
            setSearchTerm(term);
            setPage(0);
          }}
        />

        <StatusFilter
          currentStatus={selectedStatus}
          onStatusChange={(status) => {
            setSelectedStatus(status);
            setPage(0);
          }}
        />
      </div>

      {/* Content */}
      {loading ? (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          {[...Array(10)].map((_, i) => (
            <BookCardSkeleton key={i} />
          ))}
        </div>
      ) : books.length === 0 ? (
        <EmptyState
          icon={BookOpen}
          title="No books yet"
          description="Start building your library by adding your first book!"
          action={{
            label: 'Add Your First Book',
            onClick: () => setIsAddModalOpen(true),
          }}
        />
      ) : (
        <>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            {books.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-2 mt-8">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 border rounded-lg disabled:opacity-50"
              >
                Previous
              </button>

              <span className="text-gray-700">
                Page {page + 1} of {totalPages}
              </span>

              <button
                onClick={() =>
                  setPage((p) => Math.min(totalPages - 1, p + 1))
                }
                disabled={page === totalPages - 1}
                className="px-4 py-2 border rounded-lg disabled:opacity-50"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}

      {/* Add Book Modal */}
      <AddBookModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onBookAdded={() => fetchBooks(page, searchTerm, selectedStatus)}
      />
    </div>
  );
};

export default BookListPage;
