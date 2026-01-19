import React, { useEffect, useState } from 'react';
import { X } from 'lucide-react';
import type {
  Shelf,
  CreateShelfRequest,
  UpdateShelfRequest,
} from '../types/shelf';
import { shelfService } from '../services/shelfService';

interface ShelfModalProps {
  shelf?: Shelf;
  isOpen: boolean;
  onClose: () => void;
  onShelfSaved: () => void;
}

const ShelfModal: React.FC<ShelfModalProps> = ({
  shelf,
  isOpen,
  onClose,
  onShelfSaved,
}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
  });

  const isEditMode = Boolean(shelf);

  useEffect(() => {
    if (shelf) {
      setFormData({
        name: shelf.name,
        description: shelf.description ?? '',
      });
    } else {
      setFormData({
        name: '',
        description: '',
      });
    }
  }, [shelf]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (isEditMode && shelf) {
        const updateData: UpdateShelfRequest = {
          name: formData.name,
          description: formData.description || undefined,
        };
        await shelfService.updateShelf(shelf.id, updateData);
      } else {
        const createData: CreateShelfRequest = {
          name: formData.name,
          description: formData.description || undefined,
        };
        await shelfService.createShelf(createData);
      }

      onShelfSaved();
      onClose();
      setFormData({ name: '', description: '' });
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          `Failed to ${isEditMode ? 'update' : 'create'} shelf`
      );
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="w-full max-w-md rounded-lg bg-white">
        {/* Header */}
        <div className="flex items-center justify-between border-b p-6">
          <h2 className="text-2xl font-bold text-gray-900">
            {isEditMode ? 'Edit Shelf' : 'Create Shelf'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
            aria-label="Close modal"
          >
            <X className="h-6 w-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6">
          {error && (
            <div className="mb-4 rounded-lg border border-red-200 bg-red-50 p-3">
              <p className="text-sm text-red-800">{error}</p>
            </div>
          )}

          <div className="space-y-4">
            {/* Shelf Name */}
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Shelf Name *
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
                placeholder="e.g., Favorites, To Read This Month"
                className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Description */}
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                Description (optional)
              </label>
              <textarea
                value={formData.description}
                onChange={(e) =>
                  setFormData({ ...formData, description: e.target.value })
                }
                placeholder="What kind of books belong on this shelf?"
                rows={3}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Actions */}
          <div className="mt-6 flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-lg border border-gray-300 px-4 py-2 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 rounded-lg bg-blue-600 py-2 text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {loading
                ? 'Saving...'
                : isEditMode
                ? 'Save Changes'
                : 'Create Shelf'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ShelfModal;