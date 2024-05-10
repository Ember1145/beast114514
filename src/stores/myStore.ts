import { ref } from 'vue'
import { defineStore } from 'pinia'

export const myStore = defineStore('myPageStore', () => {
const emailCut = ref(''); // Initial value is an empty string
const token = ref(''); // Initial value is an empty string
const userId = ref(''); // Initial value is an empty string
const username = ref(''); // Initial value is an empty string
const avatarUrl = ref(''); // Initial value is an empty string

// Function to set the token
const setToken = (h: any) => {
  token.value = h;
}

// Function to set the email cut
const setEmailCut = (h: any) => {
  emailCut.value = h;
}

// Function to remove the token
const removeAll = () => {
  token.value = '';
  userId.value = '';
  username.value ='';
  avatarUrl.value = '';
  emailCut.value = '';
}

// Function to set the user ID
const setUserId = (h: any) => {
 userId.value=h
}

const setUsername = (name: string) => {
username.value=name
}

// Function to set the avatar URL
const setAvatarUrl = (url: string) => {
  avatarUrl.value = url;
}

return { 
  avatarUrl,
  username,
  userId,
  emailCut,
  token,
  setEmailCut,
  setToken,
  setUserId,
  setUsername,
  setAvatarUrl,
  removeAll
}
  
},{
  persist:{
    enabled:true,//持久化存储
    strategies: [
      {
        key: 'my-app-user',
        storage: localStorage
      }
    ]
},
})
