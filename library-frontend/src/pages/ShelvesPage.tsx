import React, { useEffect, useState } from 'react';
import { Plus, Library } from 'lucide-react';

import { shelfService } from '../services/shelfService';
import type { Shelf } from '../types/shelf';

import ShelfCard from '../components/ShelfCard';
import ShelfModal from '../components/ShelfModal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';

const ShelvesPage: React.FC = () => {
  const [shelves, setShelves] = useState<Shelf[]>([]);
  const [isShelfModalOpen, setIsShelfModalOpen] = useState(false);
  const [editingShelf, setEditingShelf] = useState<Shelf | undefined>();
  const [deletingShelf, setDeletingShelf] = useState<Shelf | undefined>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchShelves = async () => {
    try {
      setLoading(true);
      const data = await shelfService.getAllShelves();
      setShelves(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching shelves:', err);
      setError('Failed to load shelves');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShelves();
  }, []);

  const handleEdit = (shelf: Shelf) => {
    setEditingShelf(shelf);
    setIsShelfModalOpen(true);
  };

  const handleDelete = (shelf: Shelf) => {
    setDeletingShelf(shelf);
  };

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4">
        <p className="text-red-800">{error}</p>
      </div>
    );
  }

  return (
    <div>
      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">My Shelves</h1>
          <p className="mt-1 text-gray-600">
            Organize your books into collections
          </p>
        </div>

        <button
          onClick={() => {
            setEditingShelf(undefined);
            setIsShelfModalOpen(true);
          }}
          className="flex items-center rounded-lg bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
        >
          <Plus className="mr-2 h-5 w-5" />
          Create Shelf
        </button>
      </div>

      {/* Modals */}
      <ShelfModal
        shelf={editingShelf}
        isOpen={isShelfModalOpen}
        onClose={() => {
          setIsShelfModalOpen(false);
          setEditingShelf(undefined);
        }}
        onShelfSaved={() => {
          fetchShelves();
        }}
      />

      {deletingShelf && (
        <DeleteConfirmModal
          isOpen={true}
          title="Delete Shelf"
          message={`Are you sure you want to delete "${deletingShelf.name}"? Books on this shelf will not be deleted.`}
          onConfirm={async () => {
            await shelfService.deleteShelf(deletingShelf.id);
            setDeletingShelf(undefined);
            fetchShelves();
          }}
          onCancel={() => setDeletingShelf(undefined)}
        />
      )}

      {/* Empty State or Shelves Grid */}
      {shelves.length === 0 ? (
        <div className="py-12 text-center">
          <Library className="mx-auto mb-4 h-16 w-16 text-gray-400" />
          <h3 className="mb-2 text-lg font-medium text-gray-900">
            No shelves yet
          </h3>
          <p className="mb-4 text-gray-600">
            Create your first shelf to organize your books!
          </p>
          <button
            onClick={() => {
              setIsShelfModalOpen(true);
            }}
            className="rounded-lg bg-blue-600 px-6 py-2 text-white hover:bg-blue-700"
          >
            Create Your First Shelf
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
          {shelves.map((shelf) => (
            <ShelfCard
              key={shelf.id}
              shelf={shelf}
              onEdit={handleEdit}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default ShelvesPage;