import React from 'react';
import { ReadingStatus } from '../types/book';

interface StatusFilterProps {
  currentStatus: ReadingStatus | 'ALL';
  onStatusChange: (status: ReadingStatus | 'ALL') => void;
}

const StatusFilter: React.FC<StatusFilterProps> = ({ currentStatus, onStatusChange }) => {
  const filters = [
    { value: 'ALL', label: 'All Books' },
    { value: ReadingStatus.TO_READ, label: 'To Read' },
    { value: ReadingStatus.CURRENTLY_READING, label: 'Reading' },
    { value: ReadingStatus.FINISHED, label: 'Finished' },
  ];

  return (
    <div className="flex gap-2 flex-wrap">
      {filters.map((filter) => (
        <button
          key={filter.value}
          onClick={() => onStatusChange(filter.value as ReadingStatus | 'ALL')}
          className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
            currentStatus === filter.value
              ? 'bg-blue-600 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          {filter.label}
        </button>
      ))}
    </div>
  );
};

export default StatusFilter;