import React, { useState, useEffect } from 'react';
import { X, Target, Calendar, FileText } from 'lucide-react';
import ReadingGoalApi, {
    type ReadingGoal,
    type CreateGoalRequest,
    type UpdateGoalRequest
} from '../services/ReadingGoalApi';

interface CreateGoalModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  editGoal?: ReadingGoal | null;
}

const CreateGoalModal: React.FC<CreateGoalModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
  editGoal,
}) => {
  const currentYear = new Date().getFullYear();

  const [formData, setFormData] = useState({
    targetBooks: 52,
    year: currentYear,
    startDate: `${currentYear}-01-01`,
    endDate: `${currentYear}-12-31`,
    description: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // Populate form when editing
  useEffect(() => {
    if (editGoal) {
      setFormData({
        targetBooks: editGoal.targetBooks,
        year: editGoal.year,
        startDate: editGoal.startDate,
        endDate: editGoal.endDate,
        description: editGoal.description || '',
      });
    } else {
      // Reset form for new goal
      setFormData({
        targetBooks: 52,
        year: currentYear,
        startDate: `${currentYear}-01-01`,
        endDate: `${currentYear}-12-31`,
        description: '',
      });
    }
    setError(null);
    setSuccess(false);
  }, [editGoal, isOpen, currentYear]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'targetBooks' || name === 'year' ? parseInt(value) || 0 : value,
    }));
    setError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Validate dates
      const start = new Date(formData.startDate);
      const end = new Date(formData.endDate);

      if (end <= start) {
        setError('End date must be after start date');
        setLoading(false);
        return;
      }

      if (formData.targetBooks < 1) {
        setError('Target must be at least 1 book');
        setLoading(false);
        return;
      }

      if (editGoal) {
        // Update existing goal
        const updateRequest: UpdateGoalRequest = {
          targetBooks: formData.targetBooks,
          startDate: formData.startDate,
          endDate: formData.endDate,
          description: formData.description || undefined,
        };
        await ReadingGoalApi.updateGoal(editGoal.id, updateRequest);
      } else {
        // Create new goal
        const createRequest: CreateGoalRequest = {
          targetBooks: formData.targetBooks,
          year: formData.year,
          startDate: formData.startDate,
          endDate: formData.endDate,
          description: formData.description || undefined,
        };
        await ReadingGoalApi.createGoal(createRequest);
      }

      setSuccess(true);
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 1000);
    } catch (error: any) {
        console.error('Create goal failed:', {
          message: error.message,
          status: error.response?.status,
          data: error.response?.data,
        });
      }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
            {editGoal ? 'Edit Reading Goal' : 'Create Reading Goal'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            disabled={loading}
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Success Message */}
        {success && (
          <div className="mx-6 mt-4 p-3 bg-green-100 dark:bg-green-900 border border-green-400 dark:border-green-600 rounded-lg">
            <p className="text-sm text-green-800 dark:text-green-200">
              ✓ Goal {editGoal ? 'updated' : 'created'} successfully!
            </p>
          </div>
        )}

        {/* Error Message */}
        {error && (
          <div className="mx-6 mt-4 p-3 bg-red-100 dark:bg-red-900 border border-red-400 dark:border-red-600 rounded-lg">
            <p className="text-sm text-red-800 dark:text-red-200">{error}</p>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6">
          {/* Target Books */}
          <div className="mb-4">
            <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              <Target className="w-4 h-4" />
              Target Books
            </label>
            <input
              type="number"
              name="targetBooks"
              value={formData.targetBooks}
              onChange={handleChange}
              min="1"
              max="1000"
              required
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              placeholder="e.g., 52"
            />
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
              Number of books you want to read
            </p>
          </div>

          {/* Year (only for new goals) */}
          {!editGoal && (
            <div className="mb-4">
              <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                <Calendar className="w-4 h-4" />
                Year
              </label>
              <input
                type="number"
                name="year"
                value={formData.year}
                onChange={handleChange}
                min="2020"
                max="2100"
                required
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              />
            </div>
          )}

          {/* Start Date */}
          <div className="mb-4">
            <label className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2 block">
              Start Date
            </label>
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            />
          </div>

          {/* End Date */}
          <div className="mb-4">
            <label className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2 block">
              End Date
            </label>
            <input
              type="date"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            />
          </div>

          {/* Description */}
          <div className="mb-6">
            <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              <FileText className="w-4 h-4" />
              Description (Optional)
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows={3}
              maxLength={500}
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white resize-none"
              placeholder="e.g., Read one book per week"
            />
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
              {formData.description.length}/500 characters
            </p>
          </div>

          {/* Buttons */}
          <div className="flex gap-3">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Saving...' : editGoal ? 'Update Goal' : 'Create Goal'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateGoalModal;