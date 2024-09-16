const computeRow = (i, media) => {
    if (!media || !Array.isArray(media)) return
    if (media.length === 1) {
      return 'span 2'
    }
    if (media.length === 2) {
      return '1/3'
    }
    return media.length === 3 && i === 0 ? 'span 2' : 'span 1'
  }
  //i为当前图片索引
  const computeColumn = (media) => {
    if (!media || !Array.isArray(media)) return
    if (media.length === 1) {
      return 'span 2'
    }
  }
  const isImage = (mediaItem: string): boolean => {
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
    return imageExtensions.some((ext) => mediaItem.endsWith(ext))
  }

  export{computeColumn,computeRow,isImage}